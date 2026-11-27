package com.namii.app.features.client.establishments.data.datasources.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EstablishmentApiService {

    @GET("establishments")
    suspend fun getEstablishments(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("minLat") minLat: Double? = null,
        @Query("maxLat") maxLat: Double? = null,
        @Query("minLng") minLng: Double? = null,
        @Query("maxLng") maxLng: Double? = null
    ): List<EstablishmentDto>

    @GET("establishments/{id}")
    suspend fun getEstablishmentDetail(
        @Path("id") id: Int
    ): EstablishmentDetailDto
}
