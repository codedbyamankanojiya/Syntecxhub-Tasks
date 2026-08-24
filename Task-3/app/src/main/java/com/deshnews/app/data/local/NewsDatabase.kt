package com.deshnews.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database — single source of truth for all cached news articles.
 *
 * The concrete instance is provided by Hilt via [com.deshnews.app.di.DatabaseModule].
 * Increment [version] and supply a [androidx.room.migration.Migration] on schema changes;
 * [fallbackToDestructiveMigration] is used here only for development convenience.
 */
@Database(
    entities  = [NewsEntity::class],
    version   = 2,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {
        const val DATABASE_NAME = "deshnews_db"
    }
}
