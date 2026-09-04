package com.novachat.app.domain.repository

import com.novachat.app.domain.model.Chat
import com.novachat.app.domain.model.Message
import com.novachat.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Domain contract for all chat data operations.
 * Implementations live in the data layer and may combine Firestore,
 * Firebase Storage, and Room cache in any transparent combination.
 */
interface ChatRepository {

    /**
     * Returns a cold [Flow] emitting the current user's profile,
     * updated whenever their Firestore document changes.
     */
    fun observeCurrentUser(): Flow<User?>

    /**
     * Returns a [Flow] of a specific user's profile.
     */
    fun observeUser(uid: String): Flow<User?>

    /**
     * Returns a [Flow] of all conversations for the authenticated user,
     * ordered by [Chat.lastMessageTime] descending.
     */
    fun observeChats(): Flow<List<Chat>>

    /**
     * Returns a real-time [Flow] of messages in [chatId],
     * ordered by timestamp ascending. Room provides instant first emission;
     * Firestore keeps the stream live.
     */
    fun observeMessages(chatId: String): Flow<List<Message>>

    /**
     * Persists a text message to Firestore and updates the parent chat document.
     * Returns the generated message ID on success.
     */
    suspend fun sendTextMessage(chatId: String, content: String): Result<String>

    /**
     * Uploads [audioFile] to Firebase Storage then sends a VOICE_NOTE message
     * with pre-computed [amplitudes] and [durationMs].
     */
    suspend fun sendVoiceNote(
        chatId: String,
        audioFile: File,
        amplitudes: List<Float>,
        durationMs: Long
    ): Result<String>

    /**
     * Uploads [imageFile] to Firebase Storage then sends an IMAGE message.
     */
    suspend fun sendImage(chatId: String, imageFile: File): Result<String>

    /**
     * Broadcasts a typing indicator to all participants in [chatId].
     * Called on each keystroke and debounced at the ViewModel layer.
     */
    suspend fun sendTypingIndicator(chatId: String, isTyping: Boolean)

    /**
     * Observes whether the remote participant in [chatId] is currently typing.
     */
    fun observeTypingStatus(chatId: String, otherUserId: String): Flow<Boolean>

    /**
     * Marks all unread messages in [chatId] as read for the current user.
     */
    suspend fun markMessagesAsRead(chatId: String)

    /**
     * Creates a new 1-on-1 chat with [otherUserId], or returns the
     * existing chat ID if one already exists.
     */
    suspend fun getOrCreateChat(otherUserId: String): Result<String>

    /**
     * Updates the authenticated user's online presence and lastSeen timestamp.
     */
    suspend fun updateUserPresence(isOnline: Boolean)

    /**
     * Updates user's display name and bio in Firestore and Auth.
     */
    suspend fun updateProfile(displayName: String, bio: String): Result<Unit>

    /**
     * Directly updates the profile picture URL.
     */
    suspend fun updateProfilePictureUrl(url: String): Result<Unit>

    /**
     * Uploads a new profile picture to Storage and updates user profile.
     * Returns the new download URL.
     */
    suspend fun updateProfilePicture(imageFile: File): Result<String>

    /**
     * Removes the user's current profile picture.
     */
    suspend fun removeProfilePicture(): Result<Unit>

    /**
     * Updates a specific boolean setting in the user's profile.
     */
    suspend fun updateSetting(key: String, value: Boolean): Result<Unit>

    /**
     * Searches all registered users by [query] for the "New Chat" flow.
     */
    suspend fun searchUsers(query: String): Result<List<User>>

    /**
     * Signs in with email and password. Returns the authenticated [User].
     */
    suspend fun signInWithEmail(email: String, password: String): Result<User>

    /**
     * Creates a new account with email and password.
     */
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User>

    /**
     * Signs out the current user and clears local Room cache.
     */
    suspend fun signOut()

    /**
     * Returns the currently authenticated user's UID, or null if not signed in.
     */
    fun getCurrentUserId(): String?

    /**
     * True if a user is currently authenticated (including anonymous sessions).
     */
    fun isAuthenticated(): Boolean

    /**
     * Signs in as an anonymous guest. Returns the ephemeral [User].
     */
    suspend fun signInAnonymously(): Result<User>
}
