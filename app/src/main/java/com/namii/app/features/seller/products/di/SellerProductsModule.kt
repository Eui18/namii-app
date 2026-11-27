package com.namii.app.features.seller.products.di

import com.namii.app.features.seller.products.data.datasources.remote.SellerProductsApiService
import com.namii.app.features.seller.products.data.repositories.SellerProductsRepositoryImpl
import com.namii.app.features.seller.products.domain.repositories.SellerProductsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SellerProductsModule {

    @Binds
    @Singleton
    abstract fun bindSellerProductsRepository(
        impl: SellerProductsRepositoryImpl
    ): SellerProductsRepository

    companion object {
        @Provides
        @Singleton
        fun provideSellerProductsApiService(retrofit: Retrofit): SellerProductsApiService {
            return retrofit.create(SellerProductsApiService::class.java)
        }
    }
}
