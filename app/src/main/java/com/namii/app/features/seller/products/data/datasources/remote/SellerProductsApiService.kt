package com.namii.app.features.seller.products.data.datasources.remote

import com.google.gson.annotations.SerializedName
import com.namii.app.features.client.establishments.data.datasources.remote.ProductDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

data class SellerProductAvailabilityRequestDto(
    @SerializedName("isAvailable") val isAvailable: Boolean
)

interface SellerProductsApiService {
    @GET("seller/establishment/{establishmentId}/products")
    suspend fun getMyProducts(@Path("establishmentId") establishmentId: Int): List<ProductDto>

    @GET("seller/products/{productId}")
    suspend fun getProductById(@Path("productId") productId: Int): ProductDto

    @Multipart
    @POST("seller/establishment/{establishmentId}/products")
    suspend fun createProduct(
        @Path("establishmentId") establishmentId: Int,
        @Part("name") name: RequestBody,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("price") price: RequestBody,
        @Part("isAvailable") isAvailable: RequestBody,
        @Part photo: MultipartBody.Part
    ): ProductDto

    @Multipart
    @PUT("seller/products/{productId}")
    suspend fun updateProduct(
        @Path("productId") productId: Int,
        @Part("name") name: RequestBody,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("price") price: RequestBody,
        @Part("isAvailable") isAvailable: RequestBody,
        @Part photo: MultipartBody.Part?
    ): ProductDto

    @PATCH("seller/products/{productId}/availability")
    suspend fun toggleAvailability(
        @Path("productId") productId: Int,
        @Body body: SellerProductAvailabilityRequestDto
    ): ProductDto

    @DELETE("seller/products/{productId}")
    suspend fun deleteProduct(@Path("productId") productId: Int)
}
