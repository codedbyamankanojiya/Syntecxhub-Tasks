package com.deshnews.app.di

import com.deshnews.app.data.repository.NewsRepositoryImpl
import com.deshnews.app.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds the [NewsRepository] interface to its concrete implementation.
 *
 * Using `@Binds` (abstract function) is more efficient than `@Provides` because Hilt
 * generates no wrapper and the binding is resolved at compile time.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository
}
