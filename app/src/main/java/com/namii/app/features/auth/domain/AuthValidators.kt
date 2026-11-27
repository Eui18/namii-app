package com.namii.app.features.auth.domain

object AuthValidators {
    private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "Escribe tu nombre"
            trimmed.length > 100 -> "El nombre no puede superar 100 caracteres"
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        val cleaned = email.trim().lowercase()
        return when {
            cleaned.isEmpty() || !EMAIL_REGEX.matches(cleaned) || cleaned.length > 150 ->
                "Escribe un correo válido"
            else -> null
        }
    }

    fun validateStrongPassword(password: String): String? {
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val byteLen = password.toByteArray(Charsets.UTF_8).size
        return when {
            password.length < 8 || !hasUpper || !hasLower || !hasDigit ->
                "Debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número"
            byteLen > 72 ->
                "La contraseña es demasiado larga"
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return if (confirmPassword != password) {
            "La contraseña y su confirmación deben coincidir"
        } else {
            null
        }
    }
}
