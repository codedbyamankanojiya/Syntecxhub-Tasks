package com.syntecxhub.taskmanagement.di

import com.syntecxhub.taskmanagement.domain.repository.TaskRepository
import com.syntecxhub.taskmanagement.domain.usecase.AddTaskUseCase
import com.syntecxhub.taskmanagement.domain.usecase.DeleteTaskUseCase
import com.syntecxhub.taskmanagement.domain.usecase.GetTasksUseCase
import com.syntecxhub.taskmanagement.domain.usecase.GetTaskStatsUseCase
import com.syntecxhub.taskmanagement.domain.usecase.ToggleTaskCompletionUseCase
import com.syntecxhub.taskmanagement.domain.usecase.UpdateTaskUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetTasksUseCase(repository: TaskRepository): GetTasksUseCase {
        return GetTasksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddTaskUseCase(repository: TaskRepository): AddTaskUseCase {
        return AddTaskUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteTaskUseCase(repository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleTaskCompletionUseCase(repository: TaskRepository): ToggleTaskCompletionUseCase {
        return ToggleTaskCompletionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTaskStatsUseCase(repository: TaskRepository): GetTaskStatsUseCase {
        return GetTaskStatsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateTaskUseCase(repository: TaskRepository): UpdateTaskUseCase {
        return UpdateTaskUseCase(repository)
    }
}
