package com.novachat.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.novachat.app.data.local.converter.RoomTypeConverters
import com.novachat.app.domain.model.Message
import com.novachat.app.domain.model.MessageType

/**
 * Room database entity mirroring the [Message] domain model.
 * Stored in the `messages` table for offline-first fast cold starts.
 */
@Entity(tableName = "messages")
@TypeConverters(RoomTypeConverters::class)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String?,
    val content: String,
    val type: MessageType,
    val timestamp: Long,
    val isRead: Boolean,
    val isDelivered: Boolean = false,
    val isSentByMe: Boolean,
    val voiceAmplitudes: List<Float>,
    val voiceDurationMs: Long,
    val fileName: String?,
    val fileSize: Long?,
    val latitude: Double?,
    val longitude: Double?,
    val replyToId: String?,
    val replyPreview: String?
) {
    fun toDomain(): Message = Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        senderName = senderName,
        senderAvatar = senderAvatar,
        content = content,
        type = type,
        timestamp = timestamp,
        isRead = isRead,
        isDelivered = isDelivered,
        isSentByMe = isSentByMe,
        voiceAmplitudes = voiceAmplitudes,
        voiceDurationMs = voiceDurationMs,
        fileName = fileName,
        fileSize = fileSize,
        latitude = latitude,
        longitude = longitude,
        replyToId = replyToId,
        replyPreview = replyPreview
    )

    companion object {
        fun fromDomain(message: Message): MessageEntity = MessageEntity(
            id = message.id,
            chatId = message.chatId,
            senderId = message.senderId,
            senderName = message.senderName,
            senderAvatar = message.senderAvatar,
            content = message.content,
            type = message.type,
            timestamp = message.timestamp,
            isRead = message.isRead,
            isDelivered = message.isDelivered,
            isSentByMe = message.isSentByMe,
            voiceAmplitudes = message.voiceAmplitudes,
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
