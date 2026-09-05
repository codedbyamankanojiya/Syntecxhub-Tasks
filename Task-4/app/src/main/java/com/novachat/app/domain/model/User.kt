package com.novachat.app.domain.model

/**
 * Domain entity representing a user profile.
 *
 * @property uid         Firebase Auth UID — globally unique.
 * @property displayName Chosen username or "Guest-XXXX" for anonymous users.
 * @property email       Email address, null for anonymous guests.
 * @property photoUrl    Profile picture URL hosted on Firebase Storage.
 * @property isOnline    Real-time presence flag updated via Firestore heartbeat.
 * @property lastSeen    Unix epoch millis of the user's last recorded activity.
 * @property isAnonymous True for Firebase anonymous sign-in sessions.
 */
data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String? = null,
    val photoUrl: String? = null,
    val bio: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L,
    val readReceipts: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val lastSeenVisible: Boolean = true,
    val aboutVisible: Boolean = true,
    val isAnonymous: Boolean = false,
    val isDeleted: Boolean = false
)
