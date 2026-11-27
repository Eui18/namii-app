package com.namii.app.features.client.establishments.data.datasources.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EstablishmentApiService {
    @GET("establishments")
    suspend fun getEstablishments(
        @Query("category") category: String? = null
    ): List<EstablishmentDto>

    @GET("establishments/popular")
    suspend fun getPopularEstablishments(): List<EstablishmentDto>

    @GET("establishments/search")
    suspend fun searchEstablishments(
        @Query("q") query: String
    ): List<EstablishmentDto>

    @GET("establishments/map")
    suspend fun getEstablishmentsInMap(
        @Query("north") north: Double? = null,
        @Query("south") south: Double? = null,
        @Query("east") east: Double? = null,
        @Query("west") west: Double? = null,
        @Query("q") query: String? = null
    ): List<EstablishmentDto>

    @GET("establishments/{id}")
    suspend fun getEstablishmentDetail(
        @Path("id") id: Int
    ): EstablishmentDetailDto
}
