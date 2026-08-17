package com.wgm.quiz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WgmQuestionEntity::class], version = 2, exportSchema = false)
abstract class WgmDatabase : RoomDatabase() {
    abstract fun wgmQuestionDao(): WgmQuestionDao
}
