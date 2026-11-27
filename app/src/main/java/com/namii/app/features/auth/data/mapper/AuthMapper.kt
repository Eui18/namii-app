package com.namii.app.features.auth.data.mapper

import com.namii.app.features.auth.data.datasources.remote.AuthResponseDto
import com.namii.app.features.auth.data.datasources.remote.UserResponseDto
import com.namii.app.features.auth.domain.entities.AuthSession
import com.namii.app.features.auth.domain.entities.AuthUser
import com.namii.app.features.auth.domain.entities.UserRole

fun UserResponseDto.toDomain(): AuthUser {
    return AuthUser(
        id = id,
        name = name,
        email = email,
        role = UserRole.fromRaw(role)
    )
}

fun AuthResponseDto.toDomain(): AuthSession {
    return AuthSession(
        token = token,
        user = user.toDomain()
    )
}
