package com.novachat.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.novachat.app.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for message persistence and offline-first reads.
 */
@Dao
interface MessageDao {

    /**
     * Inserts or replaces messages — used when syncing Firestore snapshots
     * to the local cache.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessages(messages: List<MessageEntity>)

    /**
     * Returns a [Flow] of messages for [chatId] ordered by timestamp ascending.
     * Emits on every Room table update, providing instant offline reads.
     */
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun observeMessages(chatId: String): Flow<List<MessageEntity>>

    /**
     * Fetches all messages for [chatId] as a one-shot snapshot (not a Flow).
     * Used for pre-populating UI before the Firestore stream connects.
     */
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    suspend fun getMessages(chatId: String): List<MessageEntity>

    /**
     * Marks all messages in [chatId] as read.
     */
    @Query("UPDATE messages SET isRead = 1 WHERE chatId = :chatId AND isSentByMe = 0")
    suspend fun markAllAsRead(chatId: String)

    /**
     * Deletes all cached messages for [chatId] — called on sign-out for privacy.
     */
    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)

    /**
     * Deletes all messages across all chats — called on full sign-out.
     */
    @Query("DELETE FROM messages")
    suspend fun clearAll()
}
