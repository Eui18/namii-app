package com.namii.app.features.auth.domain.entities

enum class UserRole {
    CLIENTE,
    VENDEDOR;

    companion object {
        fun fromRaw(raw: String?): UserRole {
            return entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: CLIENTE
        }
    }
}

data class AuthUser(
    val id: Int,
    val name: String,
    val email: String,
    val role: UserRole
)

data class AuthSession(
    val token: String,
    val user: AuthUser
)
