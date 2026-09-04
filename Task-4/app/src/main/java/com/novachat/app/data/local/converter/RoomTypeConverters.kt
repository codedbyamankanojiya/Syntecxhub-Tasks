package com.novachat.app.data.local.converter

import androidx.room.TypeConverter
import com.novachat.app.domain.model.MessageType

/**
 * Room TypeConverters for non-primitive types that Room cannot persist natively.
 */
class RoomTypeConverters {

    // ─── MessageType ──────────────────────────────────────────────────────────

    @TypeConverter
    fun fromMessageType(type: MessageType): String = type.name

    @TypeConverter
    fun toMessageType(name: String): MessageType =
        runCatching { MessageType.valueOf(name) }.getOrDefault(MessageType.TEXT)

    // ─── List<Float> (voice amplitudes) ──────────────────────────────────────

    @TypeConverter
    fun fromFloatList(list: List<Float>): String =
        list.joinToString(separator = ",")

    @TypeConverter
    fun toFloatList(csv: String): List<Float> =
        if (csv.isBlank()) emptyList()
        else csv.split(",").mapNotNull { it.trim().toFloatOrNull() }
}
