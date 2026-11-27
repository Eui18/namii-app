package com.namii.app.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.auth.domain.AuthValidators
import com.namii.app.features.auth.domain.usecases.LoginClientUseCase
import com.namii.app.features.auth.domain.usecases.RegisterClientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthSheetMode {
    LOGIN,
    REGISTER
}

sealed interface PendingAuthAction {
    data class ToggleFavorite(val establishmentId: Int, val establishmentName: String) : PendingAuthAction
    data class RateEstablishment(
        val establishmentId: Int,
        val establishmentName: String,
        val stars: Int
    ) : PendingAuthAction
    data object OpenFavorites : PendingAuthAction
}

data class AuthSheetUiState(
    val isVisible: Boolean = false,
    val mode: AuthSheetMode = AuthSheetMode.LOGIN,
    val contextMessage: String = "",
    val pendingAction: PendingAuthAction? = null,
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
class AuthModalViewModel @Inject constructor(
    private val loginClientUseCase: LoginClientUseCase,
    private val registerClientUseCase: RegisterClientUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthSheetUiState())
    val uiState: StateFlow<AuthSheetUiState> = _uiState.asStateFlow()

    fun openLogin(contextMessage: String, pendingAction: PendingAuthAction? = null) {
        _uiState.value = AuthSheetUiState(
            isVisible = true,
            mode = AuthSheetMode.LOGIN,
            contextMessage = contextMessage,
            pendingAction = pendingAction
        )
    }

    fun openRegister(contextMessage: String, pendingAction: PendingAuthAction? = null) {
        _uiState.value = AuthSheetUiState(
            isVisible = true,
            mode = AuthSheetMode.REGISTER,
            contextMessage = contextMessage,
            pendingAction = pendingAction
        )
    }

    fun switchMode(mode: AuthSheetMode) {
        _uiState.update {
            it.copy(
                mode = mode,
                nameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                generalError = null
            )
        }
    }

    fun dismiss() {
        _uiState.value = AuthSheetUiState(isVisible = false)
    }

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

    fun submitLogin(onSuccess: (PendingAuthAction?) -> Unit) {
        val state = _uiState.value
        val emailErr = AuthValidators.validateEmail(state.email)
        val passErr = if (state.password.isBlank()) "Escribe tu contraseña" else null

        if (emailErr != null || passErr != null) {
            _uiState.update { it.copy(emailError = emailErr, passwordError = passErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, generalError = null) }
            val result = loginClientUseCase(state.email, state.password)
            result.fold(
                onSuccess = {
                    val action = _uiState.value.pendingAction
                    _uiState.value = AuthSheetUiState(isVisible = false)
                    onSuccess(action)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isSubmitting = false, generalError = error.message)
                    }
                }
            )
        }
    }

    fun submitRegister(onSuccess: (PendingAuthAction?) -> Unit) {
        val state = _uiState.value
        val nameErr = AuthValidators.validateName(state.name)
        val emailErr = AuthValidators.validateEmail(state.email)
        val passErr = AuthValidators.validateStrongPassword(state.password)
        val confirmErr = AuthValidators.validateConfirmPassword(state.password, state.confirmPassword)

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
            val result = registerClientUseCase(
                name = state.name,
                email = state.email,
                password = state.password,
                confirmPassword = state.confirmPassword
            )
            result.fold(
                onSuccess = {
                    val action = _uiState.value.pendingAction
                    _uiState.value = AuthSheetUiState(isVisible = false)
                    onSuccess(action)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isSubmitting = false, generalError = error.message)
                    }
                }
            )
        }
    }
}
