package com.novachat.app.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.FirebaseStorage
import com.novachat.app.data.local.NovaChatDatabase
import com.novachat.app.data.local.dao.MessageDao
import com.novachat.app.data.remote.FirestoreChatService
import com.novachat.app.data.repository.ChatRepositoryImpl
import com.novachat.app.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing all application-scoped dependencies.
 *
 * Architecture binding chain:
 * [ChatRepository] interface ← [ChatRepositoryImpl] ← [FirestoreChatService] + [MessageDao]
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ─── Firebase ─────────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {})
        }
        return db
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    // ─── Room Database ────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideNovaChatDatabase(
        @ApplicationContext context: Context
    ): NovaChatDatabase = Room.databaseBuilder(
        context,
        NovaChatDatabase::class.java,
        NovaChatDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideMessageDao(db: NovaChatDatabase): MessageDao = db.messageDao()

    // ─── Services ─────────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideFirestoreChatService(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): FirestoreChatService = FirestoreChatService(firestore, auth)
}

/**
 * Hilt module binding the repository interface to its implementation.
 * Kept separate to use @Binds (more efficient than @Provides for interfaces).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
