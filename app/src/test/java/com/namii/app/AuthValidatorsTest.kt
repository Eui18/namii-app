package com.namii.app

import com.namii.app.features.auth.domain.AuthValidators
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorsTest {

    @Test
    fun validateEmail_validAndInvalidCases() {
        assertNull(AuthValidators.validateEmail("rodrigo@upchiapas.edu.mx"))
        assertNotNull(AuthValidators.validateEmail(""))
        assertNotNull(AuthValidators.validateEmail("correo-invalido"))
    }

    @Test
    fun validateStrongPassword_enforcesLengthUppercaseLowercaseAndDigit() {
        assertNull(AuthValidators.validateStrongPassword("Segura123"))
        assertNotNull(AuthValidators.validateStrongPassword("corta1A"))
        assertNotNull(AuthValidators.validateStrongPassword("solominusculas1"))
        assertNotNull(AuthValidators.validateStrongPassword("SOLOMAYUSCULAS1"))
        assertNotNull(AuthValidators.validateStrongPassword("SinNumeroAa"))
    }

    @Test
    fun validateConfirmPassword_matchesExactPassword() {
        assertNull(AuthValidators.validateConfirmPassword("Segura123", "Segura123"))
        assertNotNull(AuthValidators.validateConfirmPassword("Segura123", "Otra123"))
    }
}
