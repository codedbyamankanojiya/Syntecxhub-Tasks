package com.syntecxhub.taskmanagement.data.local

import androidx.room.TypeConverter
import com.syntecxhub.taskmanagement.domain.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)
}
