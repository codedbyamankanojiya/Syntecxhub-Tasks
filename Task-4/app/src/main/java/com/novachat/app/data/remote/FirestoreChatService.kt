package com.novachat.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.novachat.app.data.remote.dto.MessageDto
import com.novachat.app.data.remote.dto.UserDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore service layer that converts snapshot listeners into reactive
 * [Flow] streams using `callbackFlow`. All Firestore I/O lives here;
 * the repository composes these streams with the Room cache.
 *
 * Collection schema:
 * ```
 * /users/{uid}                     → UserDto
 * /chats/{chatId}                  → ChatDto (metadata + lastMessage)
 * /chats/{chatId}/messages/{msgId} → MessageDto
 * /typing/{chatId}_{uid}           → { isTyping: Boolean, updatedAt: Timestamp }
 * ```
 */
@Singleton
class FirestoreChatService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // ─── Collection references ───────────────────────────────────────────────

    private val usersRef get() = firestore.collection("users")
    private val chatsRef get() = firestore.collection("chats")

    private fun messagesRef(chatId: String) =
        chatsRef.document(chatId).collection("messages")

    private fun typingRef(chatId: String) =
        firestore.collection("typing").document("${chatId}_${currentUid() ?: "anonymous"}")

    // ─── Auth helpers ────────────────────────────────────────────────────────

    fun currentUid(): String? = auth.currentUser?.uid

    // ─── User operations ─────────────────────────────────────────────────────

    /**
     * Observes the current user's Firestore profile document in real time.
     */
    fun observeCurrentUser(): Flow<UserDto?> = callbackFlow {
        val uid = currentUid()
            ?: run { trySend(null); close(); return@callbackFlow }

        val listener = usersRef.document(uid).addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.toObject(UserDto::class.java))
        }
        awaitClose { listener.remove() }
    }

    /**
     * Observes a specific user's profile document in real time.
     */
    fun observeUser(uid: String): Flow<UserDto?> = callbackFlow {
        val listener = usersRef.document(uid).addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.toObject(UserDto::class.java))
        }
        awaitClose { listener.remove() }
    }

    /**
     * Persists or updates a user profile document using merge semantics
     * so partial updates don't wipe existing fields.
     */
    suspend fun upsertUser(dto: UserDto) {
        dto.displayNameLowercase = dto.displayName.lowercase()
        usersRef.document(dto.uid).set(dto, SetOptions.merge()).await()
    }

    /**
     * Fetches a user profile by [uid] — one-shot, not a stream.
     */
    suspend fun getUser(uid: String): UserDto? =
        usersRef.document(uid).get().await().toObject(UserDto::class.java)

    /**
     * Searches users whose displayName starts with [query] (case-insensitive prefix).
     * Returns all available users if query is empty.
     */
    suspend fun searchUsers(query: String): List<UserDto> {
        val lowercaseQuery = query.lowercase().trim()
        if (lowercaseQuery.isEmpty()) {
            return usersRef
                .limit(30)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(UserDto::class.java) }
        }
        val end = lowercaseQuery + '\uF8FF'
        return usersRef
            .whereGreaterThanOrEqualTo("displayNameLowercase", lowercaseQuery)
            .whereLessThan("displayNameLowercase", end)
            .limit(30)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(UserDto::class.java) }
    }

    // ─── Chat / Conversation operations ──────────────────────────────────────

    /**
     * Observes all chats where the current user is a participant,
     * ordered by lastMessageTime descending.
     */
    fun observeChats(): Flow<List<Map<String, Any>>> = callbackFlow {
        val uid = currentUid() ?: run { trySend(emptyList()); close(); return@callbackFlow }
        val listener = chatsRef
            .whereArrayContains("participantIds", uid)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    android.util.Log.e("FirestoreChatService", "Error observing chats for uid=$uid", err)
                    close(err)
                    return@addSnapshotListener
                }
                val docs = snap?.documents?.mapNotNull { doc ->
                    doc.data?.toMutableMap()?.apply { put("id", doc.id) }
                } ?: emptyList()
                trySend(docs)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Creates a new chat document or returns the existing chat ID for a pair of users.
     * Uses a deterministic composite key (sorted UIDs joined by `_`) to prevent duplicates.
     */
    suspend fun getOrCreateChat(
        currentUserId: String,
        otherUserId: String,
        currentUserName: String,
        otherUserName: String
    ): String {
        // Deterministic chat ID prevents duplicate conversations
        val chatId = listOf(currentUserId, otherUserId).sorted().joinToString("_")
        val chatDocRef = chatsRef.document(chatId)

        // Try reading existing chat. If document doesn't exist yet or PERMISSION_DENIED is thrown
        // because resource.data is null in security rules, catch it and create the document safely.
        val chatExists = try {
            val snap = chatDocRef.get().await()
            snap.exists()
        } catch (_: Exception) {
            false
        }

        if (!chatExists) {
            val chatData = mapOf(
                "id" to chatId,
                "participantIds" to listOf(currentUserId, otherUserId),
                "participantNames" to listOf(currentUserName, otherUserName),
                "lastMessage" to "",
                "lastMessageTime" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "lastMessageSenderId" to "",
                "unreadCount" to mapOf(currentUserId to 0, otherUserId to 0)
            )
            chatDocRef.set(chatData, SetOptions.merge()).await()
        }
        return chatId
    }

    // ─── Message operations ───────────────────────────────────────────────────

    /**
     * Returns a real-time [Flow] of [MessageDto] objects for [chatId],
     * ordered by timestamp ascending. The stream stays open until cancelled.
     */
    fun observeMessages(chatId: String): Flow<List<MessageDto>> = callbackFlow {
        val listener = messagesRef(chatId)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    android.util.Log.e("FirestoreChatService", "Error observing messages for $chatId", err)
                    close(err)
                    return@addSnapshotListener
                }
                val messages = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(MessageDto::class.java)?.copy(id = doc.id)
                }?.sortedWith(compareBy({ it.timestamp?.time ?: Long.MAX_VALUE }, { it.id }))
                    ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Writes a [MessageDto] to Firestore and updates the parent chat metadata atomically.
     *
     * @return The Firestore-generated document ID.
     */
    suspend fun sendMessage(chatId: String, dto: MessageDto): String {
        val docRef = messagesRef(chatId).document()
        val dtoWithId = dto.copy(id = docRef.id)

        // Determine preview text for chat list
        val preview = when {
            dto.type == com.novachat.app.domain.model.MessageType.VOICE_NOTE.name -> "🎤 Voice message"
            dto.type == com.novachat.app.domain.model.MessageType.IMAGE.name -> "📷 Photo"
            dto.type == com.novachat.app.domain.model.MessageType.FILE.name -> "📎 ${dto.fileName ?: "File"}"
            dto.type == com.novachat.app.domain.model.MessageType.LOCATION.name -> "📍 Location"
            else -> dto.content
        }

        val otherId = chatId.split("_").firstOrNull { it != dto.senderId }
        val updates = mutableMapOf<String, Any>(
            "id" to chatId,
            "participantIds" to chatId.split("_"),
            "lastMessage" to preview,
            "lastMessageTime" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "lastMessageSenderId" to dto.senderId
        )
        if (!otherId.isNullOrEmpty()) {
            updates["unreadCount.$otherId"] = com.google.firebase.firestore.FieldValue.increment(1)
        }

        firestore.runBatch { batch ->
            // Write message to subcollection
            batch.set(docRef, dtoWithId)
            // Update chat metadata safely with SetOptions.merge
            batch.set(chatsRef.document(chatId), updates, SetOptions.merge())
        }.await()

        return docRef.id
    }

    /**
     * Marks all messages in [chatId] not sent by [currentUserId] as read.
     */
    suspend fun markMessagesAsRead(chatId: String, currentUserId: String) {
        val unreadDocs = messagesRef(chatId)
            .whereEqualTo("isRead", false)
            .get()
            .await()
            .documents
            .filter { doc -> doc.getString("senderId") != currentUserId }

        if (unreadDocs.isNotEmpty()) {
            firestore.runBatch { batch ->
                unreadDocs.forEach { doc -> batch.update(doc.reference, "isRead", true) }
                // Reset unread count for current user
                batch.set(
                    chatsRef.document(chatId),
                    mapOf("unreadCount.$currentUserId" to 0L),
                    SetOptions.merge()
                )
            }.await()
        } else {
            // Reset unread count even if no unread docs returned
            chatsRef.document(chatId).set(
                mapOf("unreadCount.$currentUserId" to 0L),
                SetOptions.merge()
            ).await()
        }
    }

    // ─── Typing indicator ─────────────────────────────────────────────────────

    /**
     * Sets the typing status for the current user in [chatId].
     */
    suspend fun setTypingStatus(chatId: String, isTyping: Boolean) {
        val uid = currentUid() ?: return
        typingRef(chatId).set(
            mapOf(
                "isTyping" to isTyping,
                "uid" to uid,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
    }

    /**
     * Observes the typing status of a specific user (not the current user) in [chatId].
     *
     * @param chatId      The chat room ID.
     * @param otherUserId The remote participant's UID whose typing state to observe.
     */
    fun observeTypingStatus(chatId: String, otherUserId: String): Flow<Boolean> = callbackFlow {
        val docRef = firestore.collection("typing").document("${chatId}_${otherUserId}")
        val listener = docRef.addSnapshotListener { snap, err ->
            if (err != null) { trySend(false); return@addSnapshotListener }
            val isTyping = snap?.getBoolean("isTyping") ?: false
            trySend(isTyping)
        }
        awaitClose { listener.remove() }
    }

    // ─── Presence ─────────────────────────────────────────────────────────────

    /**
     * Updates the current user's profile fields.
     */
    suspend fun updateProfile(displayName: String, bio: String) {
        val uid = currentUid() ?: return
        usersRef.document(uid).update(
            mapOf(
                "displayName" to displayName,
                "displayNameLowercase" to displayName.lowercase(),
                "bio" to bio
            )
        ).await()
    }

    /**
     * Updates the current user's profile picture URL.
     */
    suspend fun updateProfilePicture(url: String) {
        val uid = currentUid() ?: return
        val value: Any? = if (url.isBlank()) null else url
        usersRef.document(uid).update("photoUrl", value).await()
    }

    /**
     * Updates a single field in the user document.
     */
    suspend fun updateUserField(key: String, value: Any) {
        val uid = currentUid() ?: return
        usersRef.document(uid).update(key, value).await()
    }

    /**
     * Updates the current user's online status and lastSeen timestamp.
     */
    suspend fun updatePresence(isOnline: Boolean) {
        val uid = currentUid() ?: return
        val updates = mutableMapOf<String, Any>(
            "isOnline" to isOnline
        )
        if (!isOnline) {
            updates["lastSeen"] = com.google.firebase.firestore.FieldValue.serverTimestamp()
        }
        usersRef.document(uid).update(updates).await()
    }
}
