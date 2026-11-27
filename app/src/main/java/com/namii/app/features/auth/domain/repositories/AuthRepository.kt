package com.namii.app.features.auth.domain.repositories

import com.namii.app.features.auth.domain.entities.AuthSession
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val sessionFlow: StateFlow<AuthSession?>
    suspend fun loginClient(email: String, password: String): Result<AuthSession>
    suspend fun registerClient(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<AuthSession>
    fun logout()
}
