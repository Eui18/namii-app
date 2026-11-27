package com.namii.app.features.auth.domain.usecases

import com.namii.app.features.auth.domain.entities.AuthSession
import com.namii.app.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): StateFlow<AuthSession?> = repository.sessionFlow
}

class LoginClientUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthSession> {
        return repository.loginClient(email.trim(), password)
    }
}

class RegisterClientUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<AuthSession> {
        return repository.registerClient(name.trim(), email.trim(), password, confirmPassword)
    }
}

class LogoutClientUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.logout()
}

class LoginSellerUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthSession> {
        return repository.loginSeller(email.trim(), password)
    }
}

class RegisterSellerUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<AuthSession> {
        return repository.registerSeller(name.trim(), email.trim(), password, confirmPassword)
    }
}
