package com.namii.app.features.seller.establishments.di

import com.namii.app.features.seller.establishments.data.datasources.remote.SellerEstablishmentApiService
import com.namii.app.features.seller.establishments.data.repositories.SellerEstablishmentRepositoryImpl
import com.namii.app.features.seller.establishments.domain.repositories.SellerEstablishmentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SellerEstablishmentModule {

    @Binds
    @Singleton
    abstract fun bindSellerEstablishmentRepository(
        impl: SellerEstablishmentRepositoryImpl
    ): SellerEstablishmentRepository

    companion object {
        @Provides
        @Singleton
        fun provideSellerEstablishmentApiService(retrofit: Retrofit): SellerEstablishmentApiService {
            return retrofit.create(SellerEstablishmentApiService::class.java)
        }
    }
}
