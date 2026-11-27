package com.namii.app.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.AuthValidators
import com.namii.app.features.auth.domain.usecases.LoginSellerUseCase
import com.namii.app.features.auth.domain.usecases.ObserveSessionUseCase
import com.namii.app.features.auth.domain.usecases.RegisterSellerUseCase
import com.namii.app.features.seller.establishments.domain.usecases.GetMyEstablishmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SellerAuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isSubmitting: Boolean = false
)

@HiltViewModel
class SellerAuthViewModel @Inject constructor(
    private val loginSellerUseCase: LoginSellerUseCase,
    private val registerSellerUseCase: RegisterSellerUseCase,
    private val getMyEstablishmentUseCase: GetMyEstablishmentUseCase,
    observeSessionUseCase: ObserveSessionUseCase
) : ViewModel() {

    val sessionFlow = observeSessionUseCase()

    private val _uiState = MutableStateFlow(SellerAuthUiState())
    val uiState: StateFlow<SellerAuthUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = null, generalError = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, generalError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, generalError = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update {
            it.copy(confirmPassword = value, confirmPasswordError = null, generalError = null)
        }
    }

    fun submitLogin(onRouteToSellerHome: (hasEstablishment: Boolean) -> Unit) {
        val current = _uiState.value
        val emailErr = AuthValidators.validateEmail(current.email)
        val passErr = if (current.password.isEmpty()) "Escribe tu contraseña" else null

        if (emailErr != null || passErr != null) {
            _uiState.update { it.copy(emailError = emailErr, passwordError = passErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, generalError = null) }
            loginSellerUseCase(current.email, current.password).fold(
                onSuccess = {
                    val estResult = getMyEstablishmentUseCase()
                    estResult.fold(
                        onSuccess = { est ->
                            _uiState.update { it.copy(isSubmitting = false) }
                            onRouteToSellerHome(est != null)
                        },
                        onFailure = {
                            // Navegamos al resolvedor del vendedor para mostrar estado / reintento
                            _uiState.update { it.copy(isSubmitting = false) }
                            onRouteToSellerHome(true)
                        }
                    )
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            generalError = err.message ?: "No se pudo iniciar sesión"
                        )
                    }
                }
            )
        }
    }

    fun submitRegister(onSuccessOnboarding: () -> Unit) {
        val current = _uiState.value
        val nameErr = AuthValidators.validateName(current.name)
        val emailErr = AuthValidators.validateEmail(current.email)
        val passErr = AuthValidators.validateStrongPassword(current.password)
        val confirmErr = AuthValidators.validateConfirmPassword(current.password, current.confirmPassword)

        if (nameErr != null || emailErr != null || passErr != null || confirmErr != null) {
            _uiState.update {
                it.copy(
                    nameError = nameErr,
                    emailError = emailErr,
                    passwordError = passErr,
                    confirmPasswordError = confirmErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, generalError = null) }
            registerSellerUseCase(
                name = current.name,
                email = current.email,
                password = current.password,
                confirmPassword = current.confirmPassword
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSubmitting = false) }
                    onSuccessOnboarding()
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            generalError = err.message ?: "No se pudo registrar la cuenta"
                        )
                    }
                }
            )
        }
    }
}
