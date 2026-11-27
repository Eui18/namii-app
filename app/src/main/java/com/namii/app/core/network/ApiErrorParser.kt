package com.namii.app.core.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

private data class ApiErrorResponseDto(
    @SerializedName("message") val message: String? = null
)

@Singleton
class ApiErrorParser @Inject constructor(
    private val gson: Gson
) {
    fun parse(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                val rawBody = throwable.response()?.errorBody()?.string()
                val apiMessage = rawBody?.let {
                    runCatching { gson.fromJson(it, ApiErrorResponseDto::class.java)?.message }.getOrNull()
                }
                if (!apiMessage.isNullOrBlank()) {
                    apiMessage
                } else {
                    when (throwable.code()) {
                        400 -> "Solicitud inválida. Verifica los datos ingresados."
                        401 -> "Sesión expirada o credenciales inválidas."
                        403 -> "No tienes permiso para realizar esta operación."
                        404 -> "El recurso o servicio solicitado no está disponible."
                        409 -> "Ya existe un registro con esos datos."
                        else -> "Error del servidor (${throwable.code()}). Intenta de nuevo."
                    }
                }
            }
            is SocketTimeoutException -> "Tiempo de espera agotado al conectar con el servidor."
            is IOException -> "No se pudo conectar con el servidor. Verifica tu conexión."
            else -> throwable.localizedMessage ?: "Ocurrió un error inesperado."
        }
    }
}
