package com.namii.app.features.client.ratings.di

import com.namii.app.features.client.ratings.data.datasources.remote.RatingsApiService
import com.namii.app.features.client.ratings.data.repositories.RatingsRepositoryImpl
import com.namii.app.features.client.ratings.domain.repositories.RatingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RatingsModule {

    @Binds
    @Singleton
    abstract fun bindRatingsRepository(impl: RatingsRepositoryImpl): RatingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideRatingsApiService(retrofit: Retrofit): RatingsApiService {
            return retrofit.create(RatingsApiService::class.java)
        }
    }
}
