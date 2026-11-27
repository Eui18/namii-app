package com.namii.app.features.auth.data.repositories

import com.namii.app.core.network.ApiErrorParser
import com.namii.app.features.auth.data.datasources.local.TokenPreferencesDataSource
import com.namii.app.features.auth.data.datasources.remote.AuthApiService
import com.namii.app.features.auth.data.datasources.remote.LoginRequestDto
import com.namii.app.features.auth.data.datasources.remote.RegisterRequestDto
import com.namii.app.features.auth.data.mapper.toDomain
import com.namii.app.features.auth.domain.entities.AuthSession
import com.namii.app.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val localDataSource: TokenPreferencesDataSource,
    private val errorParser: ApiErrorParser
) : AuthRepository {

    override val sessionFlow: StateFlow<AuthSession?> = localDataSource.sessionFlow

    override suspend fun loginClient(email: String, password: String): Result<AuthSession> {
        return try {
            val response = apiService.loginClient(
                LoginRequestDto(email = email.trim().lowercase(), password = password)
            )
            val session = response.toDomain()
            localDataSource.saveSession(session)
            Result.success(session)
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override suspend fun registerClient(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<AuthSession> {
        return try {
            val response = apiService.registerClient(
                RegisterRequestDto(
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    password = password,
                    confirmPassword = confirmPassword
                )
            )
            val session = response.toDomain()
            localDataSource.saveSession(session)
            Result.success(session)
        } catch (t: Throwable) {
            Result.failure(Exception(errorParser.parse(t)))
        }
    }

    override fun logout() {
        localDataSource.clearSession()
    }
}
