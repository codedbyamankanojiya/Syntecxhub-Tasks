package com.novachat.app.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.novachat.app.data.local.dao.MessageDao
import com.novachat.app.data.local.entity.MessageEntity
import com.novachat.app.data.remote.FirestoreChatService
import com.novachat.app.data.remote.dto.MessageDto
import com.novachat.app.data.remote.dto.UserDto
import com.novachat.app.domain.model.Chat
import com.novachat.app.domain.model.Message
import com.novachat.app.domain.model.MessageType
import com.novachat.app.domain.model.User
import com.novachat.app.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [ChatRepository] that fuses three data sources:
 *
 * 1. **Firestore** — real-time authoritative source via [FirestoreChatService].
 * 2. **Firebase Storage** — binary asset hosting for voice notes and images.
 * 3. **Room** — local SQLite cache for instant cold-start reads and offline use.
 *
 * Data flow for message observation:
 * ```
 * Firestore snapshot listener (callbackFlow)
 *     → writes to Room via [applicationScope]
 *         → Room Flow emits to UI (offline-first)
 * ```
 */
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreChatService,
    private val messageDao: MessageDao,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ChatRepository {

    /**
     * Application-scoped coroutine scope that outlives any ViewModel.
     * Used for the Firestore → Room sync background job.
     */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val activeSyncJobs = mutableMapOf<String, Job>()

    // ─── Auth ────────────────────────────────────────────────────────────────

    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    override fun isAuthenticated(): Boolean = auth.currentUser != null

    override suspend fun signInWithEmail(email: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            runCatching {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val fbUser = result.user ?: error("Auth returned null user")

                val name = fbUser.displayName ?: email.substringBefore("@")
                val dto = UserDto(
                    uid = fbUser.uid,
                    displayName = name,
                    displayNameLowercase = name.lowercase(),
                    email = fbUser.email,
                    photoUrl = fbUser.photoUrl?.toString(),
                    isOnline = true,
                    isAnonymous = false
                )
                firestoreService.upsertUser(dto)
                dto.toDomain()
            }
        }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val fbUser = result.user ?: error("Auth returned null user")

            fbUser.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
            ).await()

            val dto = UserDto(
                uid = fbUser.uid,
                displayName = displayName,
                displayNameLowercase = displayName.lowercase(),
                email = email,
                isOnline = true,
                isAnonymous = false
            )
            firestoreService.upsertUser(dto)
            dto.toDomain()
        }
    }

    override suspend fun signInAnonymously(): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            val result = auth.signInAnonymously().await()
            val fbUser = result.user ?: error("Anonymous auth returned null user")
            val guestName = "Guest-${fbUser.uid.take(6).uppercase()}"

            val dto = UserDto(
                uid = fbUser.uid,
                displayName = guestName,
                displayNameLowercase = guestName.lowercase(),
                email = null,
                isOnline = true,
                isAnonymous = true
            )
            firestoreService.upsertUser(dto)
            dto.toDomain()
        }
    }


    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            runCatching { firestoreService.updatePresence(false) }
            auth.signOut()
            messageDao.clearAll()
        }
    }

    override suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val uid = getCurrentUserId() ?: error("Not authenticated")

            // 1. Mark user in Firestore as deleted with "Deleted User" identity
            runCatching { firestoreService.markUserDeleted(uid) }

            // 2. Propagate "Deleted User" to all conversation documents
            runCatching { firestoreService.propagateNameToChats("Deleted User") }

            // 3. Clear local Room database cache
            messageDao.clearAll()

            // 4. Delete Firebase Auth account
            val currentUser = auth.currentUser
            if (currentUser != null) {
                runCatching { currentUser.delete().await() }
            }

            // 5. Unconditionally sign out so session is completely cleared on device
            auth.signOut()

            Unit
        }
    }

    // ─── User ─────────────────────────────────────────────────────────────────

    override fun observeCurrentUser(): Flow<User?> =
        firestoreService.observeCurrentUser().map { it?.toDomain() }

    override fun observeUser(uid: String): Flow<User?> =
        firestoreService.observeUser(uid).map { it?.toDomain() }

    override suspend fun updateUserPresence(isOnline: Boolean) {
        withContext(Dispatchers.IO) {
            runCatching { firestoreService.updatePresence(isOnline) }
        }
    }

    override suspend fun updateProfile(displayName: String, bio: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                // Update Firebase Auth profile
                auth.currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                )?.await()

                // Update Firestore profile
                firestoreService.updateProfile(displayName, bio)

                // Propagate updated name to existing chats
                firestoreService.propagateNameToChats(displayName)
            }
        }

    override suspend fun updateProfilePictureUrl(url: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                // Update Auth
                auth.currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(url))
                        .build()
                )?.await()

                // Update Firestore
                firestoreService.updateProfilePicture(url)
            }
        }

    override suspend fun updateProfilePicture(imageFile: File): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val uid = getCurrentUserId() ?: error("Not authenticated")
                
                // Downscale & compress image to <= 512x512 JPEG for fast loading & small payload
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    ?: error("Failed to decode image file")
                
                val maxDim = 512
                val scale = minOf(1f, maxDim.toFloat() / maxOf(bitmap.width, bitmap.height))
                val scaledBitmap = if (scale < 1f) {
                    Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * scale).toInt(),
                        (bitmap.height * scale).toInt(),
                        true
                    )
                } else bitmap

                val outStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream)
                val compressedBytes = outStream.toByteArray()

                val fileName = "avatar_${uid}_${System.currentTimeMillis()}.jpg"
                
                val finalUrl: String = try {
                    val ref = storage.reference.child("avatars/$uid/$fileName")
                    val metadata = com.google.firebase.storage.StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build()
                    ref.putBytes(compressedBytes, metadata).await()
                    ref.downloadUrl.await().toString()
                } catch (e: Exception) {
                    android.util.Log.e("ChatRepositoryImpl", "Storage upload failed, falling back to base64", e)
                    val base64 = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
                    "data:image/jpeg;base64,$base64"
                }

                // Update Auth
                runCatching {
                    auth.currentUser?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(finalUrl))
                            .build()
                    )?.await()
                }

                // Update Firestore
                firestoreService.updateProfilePicture(finalUrl)
                finalUrl
            }
        }

    override suspend fun removeProfilePicture(): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!isAuthenticated()) error("Not authenticated")
                runCatching {
                    auth.currentUser?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setPhotoUri(null)
                            .build()
                    )?.await()
                }
                firestoreService.updateProfilePicture("")
            }
        }

    override suspend fun updateSetting(key: String, value: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { firestoreService.updateUserField(key, value) }
        }

    override suspend fun searchUsers(query: String): Result<List<User>> =
        withContext(Dispatchers.IO) {
            runCatching { firestoreService.searchUsers(query).map { it.toDomain() } }
        }

    // ─── Chats ────────────────────────────────────────────────────────────────

    override fun observeChats(): Flow<List<Chat>> =
        firestoreService.observeChats().map { rawList ->
            val currentUid = getCurrentUserId() ?: return@map emptyList()
            rawList.mapNotNull { data ->
                runCatching {
                    val id = data["id"] as? String ?: return@mapNotNull null
                    val participantIds = (data["participantIds"] as? List<*>)
                        ?.filterIsInstance<String>() ?: emptyList()
                    val participantNames = (data["participantNames"] as? List<*>)
                        ?.filterIsInstance<String>() ?: emptyList()

                    val otherIndex = participantIds.indexOfFirst { it != currentUid }
                    val otherUserId = participantIds.getOrNull(otherIndex) ?: ""
                    val otherUser = runCatching {
                        if (otherUserId.isNotEmpty()) firestoreService.getUser(otherUserId) else null
                    }.getOrNull()

                    val isDeletedUser = otherUser?.isDeleted == true || otherUser?.displayName == "Deleted User"
                    val otherUserNameFallback = participantNames.getOrNull(otherIndex)?.takeIf { it.isNotBlank() } ?: "User"
                    val resolvedName = if (isDeletedUser) "Deleted User" else (otherUser?.displayName?.takeIf { it.isNotBlank() } ?: otherUserNameFallback)
                    val resolvedAvatar = if (isDeletedUser) null else otherUser?.photoUrl?.takeIf { it.isNotBlank() }
                    val isOnline = if (isDeletedUser) false else (otherUser?.isOnline ?: false)

                    val lastMsgTime = when (val ts = data["lastMessageTime"]) {
                        is com.google.firebase.Timestamp -> ts.toDate().time
                        is Long -> ts
                        else -> 0L
                    }

                    val rawUnread = (data["unreadCount"] as? Map<*, *>)?.get(currentUid)
                        ?: data["unreadCount.$currentUid"]
                    val unreadCount = when (rawUnread) {
                        is Number -> rawUnread.toInt()
                        else -> 0
                    }

                    Chat(
                        id = id,
                        participantIds = participantIds,
                        participantNames = participantNames,
                        otherUserName = resolvedName,
                        otherUserAvatar = resolvedAvatar,
                        otherUserOnline = isOnline,
                        lastMessage = data["lastMessage"] as? String ?: "",
                        lastMessageTime = lastMsgTime,
                        unreadCount = unreadCount,
                        lastMessageSenderId = data["lastMessageSenderId"] as? String ?: ""
                    )
                }.getOrNull()
            }.sortedByDescending { it.lastMessageTime }
        }

    override suspend fun getOrCreateChat(otherUserId: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val currentUid = getCurrentUserId() ?: error("Not authenticated")
                val currentUser = runCatching { firestoreService.getUser(currentUid) }.getOrNull()
                val otherUser = runCatching { firestoreService.getUser(otherUserId) }.getOrNull()

                val currentName = currentUser?.displayName ?: auth.currentUser?.displayName ?: "User"
                val otherName = otherUser?.displayName ?: "User"

                firestoreService.getOrCreateChat(
                    currentUserId = currentUid,
                    otherUserId = otherUserId,
                    currentUserName = currentName,
                    otherUserName = otherName
                )
            }
        }

    // ─── Messages ─────────────────────────────────────────────────────────────

    /**
     * Offline-first message stream.
     *
     * Strategy:
     * 1. Room DAO emits cached messages immediately (zero-latency cold start).
     * 2. A separate coroutine on [applicationScope] listens to Firestore and
     *    writes updates back to Room.
     * 3. Room re-emits, keeping the primary UI stream from the local database.
     */
    override fun observeMessages(chatId: String): Flow<List<Message>> {
        val currentUid = getCurrentUserId() ?: ""

        // Cancel any previous sync for this chat
        activeSyncJobs[chatId]?.cancel()
        activeSyncJobs[chatId] = applicationScope.launch {
            firestoreService.observeMessages(chatId)
                .catch { e ->
                    android.util.Log.e("ChatRepositoryImpl", "observeMessages sync error for $chatId", e)
                }
                .collect { dtos ->
                    val entities = dtos.map { dto ->
                        MessageEntity.fromDomain(dto.toDomain(currentUid))
                    }
                    messageDao.upsertMessages(entities)

                    if (currentUid.isNotEmpty() && dtos.any { it.senderId != currentUid && !it.isDelivered }) {
                        firestoreService.markMessagesAsDelivered(chatId, currentUid)
                    }
                }
        }

        // Primary stream from Room (offline-capable, instant)
        return messageDao.observeMessages(chatId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun sendTextMessage(chatId: String, content: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val uid = getCurrentUserId() ?: error("Not authenticated")
                val user = runCatching { firestoreService.getUser(uid) }.getOrNull()
                val senderName = user?.displayName?.takeIf { it.isNotBlank() }
                    ?: auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
                    ?: "User"
                val senderAvatar = user?.photoUrl?.takeIf { it.isNotBlank() }
                    ?: auth.currentUser?.photoUrl?.toString()

                val msgId = firestoreService.sendMessage(
                    chatId = chatId,
                    dto = MessageDto(
                        chatId = chatId,
                        senderId = uid,
                        senderName = senderName,
                        senderAvatar = senderAvatar,
                        content = content,
                        type = MessageType.TEXT.name
                    )
                )

                // Optimistically insert into Room immediately
                val localEntity = MessageEntity(
                    id = msgId,
                    chatId = chatId,
                    senderId = uid,
                    senderName = senderName,
                    senderAvatar = senderAvatar,
                    content = content,
                    type = MessageType.TEXT,
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    isSentByMe = true,
                    voiceAmplitudes = emptyList(),
                    voiceDurationMs = 0L,
                    fileName = null,
                    fileSize = null,
                    latitude = null,
                    longitude = null,
                    replyToId = null,
                    replyPreview = null
                )
                messageDao.upsertMessages(listOf(localEntity))

                msgId
            }
        }

    override suspend fun sendVoiceNote(
        chatId: String,
        audioFile: File,
        amplitudes: List<Float>,
        durationMs: Long
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val uid = getCurrentUserId() ?: error("Not authenticated")
            val user = firestoreService.getUser(uid) ?: error("User profile missing")

            val fileName = "voice_${UUID.randomUUID()}.m4a"
            val ref = storage.reference.child("voice_notes/$chatId/$fileName")
            ref.putFile(Uri.fromFile(audioFile)).await()
            val url = ref.downloadUrl.await().toString()

            firestoreService.sendMessage(
                chatId = chatId,
                dto = MessageDto(
                    chatId = chatId,
                    senderId = uid,
                    senderName = user.displayName,
                    senderAvatar = user.photoUrl,
                    content = url,
                    type = MessageType.VOICE_NOTE.name,
                    voiceAmplitudes = amplitudes.map { it.toDouble() },
                    voiceDurationMs = durationMs
                )
            )
        }
    }

    override suspend fun sendImage(chatId: String, imageFile: File): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val uid = getCurrentUserId() ?: error("Not authenticated")
                val user = firestoreService.getUser(uid) ?: error("User profile missing")

                val fileName = "img_${UUID.randomUUID()}.jpg"
                val ref = storage.reference.child("images/$chatId/$fileName")
                ref.putFile(Uri.fromFile(imageFile)).await()
                val url = ref.downloadUrl.await().toString()

                firestoreService.sendMessage(
                    chatId = chatId,
                    dto = MessageDto(
                        chatId = chatId,
                        senderId = uid,
                        senderName = user.displayName,
                        senderAvatar = user.photoUrl,
                        content = url,
                        type = MessageType.IMAGE.name
                    )
                )
            }
        }

    // ─── Typing ───────────────────────────────────────────────────────────────

    override suspend fun sendTypingIndicator(chatId: String, isTyping: Boolean) {
        withContext(Dispatchers.IO) {
            runCatching { firestoreService.setTypingStatus(chatId, isTyping) }
        }
    }

    override fun observeTypingStatus(chatId: String, otherUserId: String): Flow<Boolean> =
        firestoreService.observeTypingStatus(chatId, otherUserId)

    override suspend fun markMessagesAsRead(chatId: String) {
        withContext(Dispatchers.IO) {
            val uid = getCurrentUserId() ?: return@withContext
            runCatching {
                firestoreService.markMessagesAsRead(chatId, uid)
                messageDao.markAllAsRead(chatId)
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                auth.sendPasswordResetEmail(email).await()
                Unit
            }
        }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val user = auth.currentUser ?: error("Not authenticated")
                val email = user.email ?: error("No email associated with account")
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
                Unit
            }
        }

    override suspend fun changeEmail(newEmail: String, currentPassword: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val user = auth.currentUser ?: error("Not authenticated")
                val oldEmail = user.email ?: error("No email associated with account")
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(oldEmail, currentPassword)
                user.reauthenticate(credential).await()

                try {
                    user.verifyBeforeUpdateEmail(newEmail).await()
                } catch (e: NoSuchMethodError) {
                    @Suppress("DEPRECATION")
                    user.updateEmail(newEmail).await()
                } catch (e: Exception) {
                    @Suppress("DEPRECATION")
                    runCatching { user.updateEmail(newEmail).await() }.getOrThrow()
                }

                firestoreService.updateUserField("email", newEmail)
                Unit
            }
        }

    override suspend fun markMessagesAsDelivered(chatId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val uid = getCurrentUserId() ?: error("Not authenticated")
                firestoreService.markMessagesAsDelivered(chatId, uid)
                Unit
            }
        }
}
