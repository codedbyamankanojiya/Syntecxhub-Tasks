package com.novachat.app.data.remote.dto

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.novachat.app.domain.model.Message
import com.novachat.app.domain.model.MessageType
import java.util.Date

/**
 * Firestore Data Transfer Object for a chat message document.
 *
 * All field names match the Firestore document schema exactly.
 * The no-arg constructor is required by the Firestore SDK for automatic
 * deserialization via reflection.
 */
data class MessageDto(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("chatId")
    @set:PropertyName("chatId")
    var chatId: String = "",

    @get:PropertyName("senderId")
    @set:PropertyName("senderId")
    var senderId: String = "",

    @get:PropertyName("senderName")
    @set:PropertyName("senderName")
    var senderName: String = "",

    @get:PropertyName("senderAvatar")
    @set:PropertyName("senderAvatar")
    var senderAvatar: String? = null,

    @get:PropertyName("content")
    @set:PropertyName("content")
    var content: String = "",

    @get:PropertyName("type")
    @set:PropertyName("type")
    var type: String = MessageType.TEXT.name,

    @ServerTimestamp
    @get:PropertyName("timestamp")
    @set:PropertyName("timestamp")
    var timestamp: Date? = null,

    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean = false,

    @get:PropertyName("isDelivered")
    @set:PropertyName("isDelivered")
    var isDelivered: Boolean = false,

    @get:PropertyName("voiceAmplitudes")
    @set:PropertyName("voiceAmplitudes")
    var voiceAmplitudes: List<Double> = emptyList(),

    @get:PropertyName("voiceDurationMs")
    @set:PropertyName("voiceDurationMs")
    var voiceDurationMs: Long = 0L,

    @get:PropertyName("fileName")
    @set:PropertyName("fileName")
    var fileName: String? = null,

    @get:PropertyName("fileSize")
    @set:PropertyName("fileSize")
    var fileSize: Long? = null,

    @get:PropertyName("latitude")
    @set:PropertyName("latitude")
    var latitude: Double? = null,

    @get:PropertyName("longitude")
    @set:PropertyName("longitude")
    var longitude: Double? = null,

    @get:PropertyName("replyToId")
    @set:PropertyName("replyToId")
    var replyToId: String? = null,

    @get:PropertyName("replyPreview")
    @set:PropertyName("replyPreview")
    var replyPreview: String? = null
) {
    /**
     * Converts this DTO into the canonical [Message] domain entity.
     *
     * @param currentUserId UID of the authenticated user — used to set [Message.isSentByMe].
     */
    fun toDomain(currentUserId: String): Message {
        return Message(
            id = id,
            chatId = chatId,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            type = runCatching { MessageType.valueOf(type) }.getOrDefault(MessageType.TEXT),
            timestamp = timestamp?.time ?: System.currentTimeMillis(),
            isRead = isRead,
            isDelivered = isDelivered,
            isSentByMe = senderId == currentUserId,
            voiceAmplitudes = voiceAmplitudes.map { it.toFloat() },
            voiceDurationMs = voiceDurationMs,
            fileName = fileName,
            fileSize = fileSize,
            latitude = latitude,
            longitude = longitude,
            replyToId = replyToId,
            replyPreview = replyPreview
        )
    }

    companion object {
        /**
         * Creates a [MessageDto] from a domain [Message] ready for Firestore persistence.
         */
        fun fromDomain(message: Message): MessageDto = MessageDto(
            id = message.id,
            chatId = message.chatId,
            senderId = message.senderId,
            senderName = message.senderName,
            senderAvatar = message.senderAvatar,
            content = message.content,
            type = message.type.name,
            isRead = message.isRead,
            isDelivered = message.isDelivered,
            voiceAmplitudes = message.voiceAmplitudes.map { it.toDouble() },
            voiceDurationMs = message.voiceDurationMs,
            fileName = message.fileName,
            fileSize = message.fileSize,
            latitude = message.latitude,
            longitude = message.longitude,
            replyToId = message.replyToId,
            replyPreview = message.replyPreview
        )
    }
}
