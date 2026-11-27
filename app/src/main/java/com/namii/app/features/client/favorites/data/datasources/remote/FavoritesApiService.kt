package com.namii.app.features.client.favorites.data.datasources.remote

import com.namii.app.features.client.establishments.data.datasources.remote.EstablishmentDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoritesApiService {
    @GET("favorites")
    suspend fun getFavorites(): List<EstablishmentDto>

    @POST("favorites/{establishmentId}")
    suspend fun addFavorite(@Path("establishmentId") establishmentId: Int): Response<Unit>

    @DELETE("favorites/{establishmentId}")
    suspend fun removeFavorite(@Path("establishmentId") establishmentId: Int): Response<Unit>
}
