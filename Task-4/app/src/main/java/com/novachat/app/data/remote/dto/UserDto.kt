package com.novachat.app.data.remote.dto

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.novachat.app.domain.model.User
import java.util.Date

/**
 * Firestore Data Transfer Object for a user profile document.
 *
 * Maps to the `/users/{uid}` Firestore collection.
 * No-arg constructor required for Firestore automatic deserialization.
 */
data class UserDto(
    @get:PropertyName("uid")
    @set:PropertyName("uid")
    var uid: String = "",

    @get:PropertyName("displayName")
    @set:PropertyName("displayName")
    var displayName: String = "",

    @get:PropertyName("displayNameLowercase")
    @set:PropertyName("displayNameLowercase")
    var displayNameLowercase: String = "",

    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String? = null,

    @get:PropertyName("photoUrl")
    @set:PropertyName("photoUrl")
    var photoUrl: String? = null,

    @get:PropertyName("bio")
    @set:PropertyName("bio")
    var bio: String = "",

    @get:PropertyName("isOnline")
    @set:PropertyName("isOnline")
    var isOnline: Boolean = false,

    @ServerTimestamp
    @get:PropertyName("lastSeen")
    @set:PropertyName("lastSeen")
    var lastSeen: Date? = null,

    @get:PropertyName("readReceipts")
    @set:PropertyName("readReceipts")
    var readReceipts: Boolean = true,

    @get:PropertyName("notificationsEnabled")
    @set:PropertyName("notificationsEnabled")
    var notificationsEnabled: Boolean = true,

    @get:PropertyName("lastSeenVisible")
    @set:PropertyName("lastSeenVisible")
    var lastSeenVisible: Boolean = true,

    @get:PropertyName("aboutVisible")
    @set:PropertyName("aboutVisible")
    var aboutVisible: Boolean = true,

    @get:PropertyName("isAnonymous")
    @set:PropertyName("isAnonymous")
    var isAnonymous: Boolean = false,

    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    var isDeleted: Boolean = false,

    @get:PropertyName("fcmToken")
    @set:PropertyName("fcmToken")
    var fcmToken: String? = null
) {
    /** Converts this DTO into the canonical [User] domain entity. */
    fun toDomain(): User = User(
        uid = uid,
        displayName = displayName,
        email = email,
        photoUrl = photoUrl?.takeIf { it.isNotBlank() },
        bio = bio,
        isOnline = isOnline,
        lastSeen = lastSeen?.time ?: 0L,
        readReceipts = readReceipts,
        notificationsEnabled = notificationsEnabled,
        lastSeenVisible = lastSeenVisible,
        aboutVisible = aboutVisible,
        isAnonymous = isAnonymous,
        isDeleted = isDeleted
    )

    companion object {
        /** Creates a [UserDto] from a domain [User]. */
        fun fromDomain(user: User): UserDto = UserDto(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl,
            isOnline = user.isOnline,
            isAnonymous = user.isAnonymous,
            isDeleted = user.isDeleted
        )
    }
}
