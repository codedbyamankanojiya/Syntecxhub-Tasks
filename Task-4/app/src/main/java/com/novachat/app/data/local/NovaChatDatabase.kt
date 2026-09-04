package com.novachat.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.novachat.app.data.local.converter.RoomTypeConverters
import com.novachat.app.data.local.dao.MessageDao
import com.novachat.app.data.local.entity.MessageEntity

/**
 * NovaChat Room database — version 1.
 *
 * Provides DAOs for all locally cached entities. The database file
 * is created under the standard app data directory.
 */
@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class NovaChatDatabase : RoomDatabase() {

    /** Returns the [MessageDao] for message persistence. */
    abstract fun messageDao(): MessageDao

    companion object {
        const val DATABASE_NAME = "novachat_db"
    }
}
