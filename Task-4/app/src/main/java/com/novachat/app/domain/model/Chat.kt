package com.novachat.app.domain.model

/**
 * Represents a conversation entry as shown in the [ChatListScreen].
 *
 * @property id                Firestore chat document ID.
 * @property participantIds    List of UIDs involved in this conversation.
 * @property participantNames  Participant display names for quick rendering.
 * @property otherUserName     Display name of the remote participant (1-on-1 chats).
 * @property otherUserAvatar   Avatar URL of the remote participant.
 * @property otherUserOnline   Real-time online presence of the remote participant.
 * @property lastMessage       Text preview or "[Voice Note]" / "[Image]" placeholder.
 * @property lastMessageTime   Timestamp for sorting and relative display.
 * @property unreadCount       Count of unread messages for the current user.
 * @property lastMessageSenderId UID who sent the last message (for "You:" prefix).
 */
data class Chat(
    val id: String = "",
    val participantIds: List<String> = emptyList(),
    val participantNames: List<String> = emptyList(),
    val otherUserName: String = "",
    val otherUserAvatar: String? = null,
    val otherUserOnline: Boolean = false,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val unreadCount: Int = 0,
    val lastMessageSenderId: String = ""
)
