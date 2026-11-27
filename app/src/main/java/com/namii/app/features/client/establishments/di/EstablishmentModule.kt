package com.namii.app.features.client.establishments.di

import com.namii.app.features.client.establishments.data.datasources.remote.EstablishmentApiService
import com.namii.app.features.client.establishments.data.repositories.EstablishmentRepositoryImpl
import com.namii.app.features.client.establishments.domain.repositories.EstablishmentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EstablishmentModule {

    @Binds
    @Singleton
    abstract fun bindEstablishmentRepository(impl: EstablishmentRepositoryImpl): EstablishmentRepository

    companion object {
        @Provides
        @Singleton
        fun provideEstablishmentApiService(retrofit: Retrofit): EstablishmentApiService {
            return retrofit.create(EstablishmentApiService::class.java)
        }
    }
}
