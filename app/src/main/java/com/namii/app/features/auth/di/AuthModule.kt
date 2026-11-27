package com.namii.app.features.auth.di

import com.namii.app.core.network.TokenProvider
import com.namii.app.features.auth.data.datasources.local.TokenPreferencesDataSource
import com.namii.app.features.auth.data.datasources.remote.AuthApiService
import com.namii.app.features.auth.data.repositories.AuthRepositoryImpl
import com.namii.app.features.auth.domain.repositories.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindTokenProvider(impl: TokenPreferencesDataSource): TokenProvider

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
            return retrofit.create(AuthApiService::class.java)
        }
    }
}
