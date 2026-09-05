package com.novachat.app.domain.model

/**
 * Core domain entity representing a single chat message.
 * This is the source of truth across all layers — DTOs and Room
 * entities map to/from this class.
 *
 * @property id           Firestore document ID (also used as Room primary key).
 * @property chatId       Identifier of the parent chat/conversation document.
 * @property senderId     UID of the user who authored this message.
 * @property senderName   Display name resolved at send time (denormalized for speed).
 * @property senderAvatar Optional URL to sender's profile photo.
 * @property content      Primary message body — plain text or storage URL for media.
 * @property type         Content type enumeration driving bubble render strategy.
 * @property timestamp    Unix epoch milliseconds (UTC) set server-side.
 * @property isRead       True once every participant has seen the message.
 * @property isSentByMe   Convenience flag set at the repository layer based on current UID.
 * @property voiceAmplitudes Normalized amplitude samples [0f..1f] for waveform rendering.
 * @property voiceDurationMs Total voice note playback duration in milliseconds.
 * @property fileName     Original filename for FILE type attachments.
 * @property fileSize     File size in bytes for FILE type attachments.
 * @property latitude     Latitude for LOCATION messages.
 * @property longitude    Longitude for LOCATION messages.
 * @property replyToId    ID of the message being replied to, null if not a reply.
 * @property replyPreview Short preview text from the replied-to message.
 */
data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String? = null,
    val content: String = "",
    val type: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
    val isSentByMe: Boolean = false,
    val voiceAmplitudes: List<Float> = emptyList(),
    val voiceDurationMs: Long = 0L,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val replyToId: String? = null,
    val replyPreview: String? = null
)
