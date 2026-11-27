package com.namii.app.features.client.favorites.di

import com.namii.app.features.client.favorites.data.datasources.remote.FavoritesApiService
import com.namii.app.features.client.favorites.data.repositories.FavoritesRepositoryImpl
import com.namii.app.features.client.favorites.domain.repositories.FavoritesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesModule {

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    companion object {
        @Provides
        @Singleton
        fun provideFavoritesApiService(retrofit: Retrofit): FavoritesApiService {
            return retrofit.create(FavoritesApiService::class.java)
        }
    }
}
