package com.namii.app.features.client.map.di

import com.namii.app.features.client.map.data.repositories.ClientMapRepositoryImpl
import com.namii.app.features.client.map.domain.repositories.ClientMapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ClientMapModule {

    @Binds
    @Singleton
    abstract fun bindClientMapRepository(impl: ClientMapRepositoryImpl): ClientMapRepository
}
