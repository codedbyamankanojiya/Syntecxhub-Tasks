package com.syntecxhub.taskmanagement.di

import com.syntecxhub.taskmanagement.data.local.TaskDao
import com.syntecxhub.taskmanagement.data.repository.TaskRepositoryImpl
import com.syntecxhub.taskmanagement.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(dao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(dao)
    }
}
