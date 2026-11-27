package com.namii.app.features.auth.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namii.app.R
import com.namii.app.features.auth.presentation.viewmodels.AuthSheetMode
import com.namii.app.features.auth.presentation.viewmodels.AuthSheetUiState
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.CacaoDark
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.ErrorSurface
import com.namii.app.ui.theme.InputBackground
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.TextoMuted
import com.namii.app.ui.theme.TextoPrincipal
import com.namii.app.ui.theme.TextoSecundario

/**
 * Vistas 8 y 9: Hojas inferiores contextuales para Inicio de sesión y Registro del cliente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientAuthBottomSheets(
    state: AuthSheetUiState,
    onDismiss: () -> Unit,
    onSwitchMode: (AuthSheetMode) -> Unit,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSubmitLogin: () -> Unit,
    onSubmitRegister: () -> Unit
) {
    if (!state.isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PureWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = state.contextMessage.ifBlank {
                    if (state.mode == AuthSheetMode.LOGIN) {
                        stringResource(R.string.btn_login)
                    } else {
                        stringResource(R.string.btn_create_account)
                    }
                },
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (state.mode == AuthSheetMode.LOGIN) {
                    stringResource(R.string.auth_default_login_subtitle)
                } else {
                    stringResource(R.string.auth_default_register_subtitle)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.generalError != null) {
                Surface(
                    color = ErrorSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.generalError,
                        color = ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.mode == AuthSheetMode.REGISTER) {
                AuthFormField(
                    label = stringResource(R.string.label_name),
                    value = state.name,
                    onValueChange = onNameChanged,
                    error = state.nameError,
                    keyboardType = KeyboardType.Text
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            AuthFormField(
                label = stringResource(R.string.label_email),
                value = state.email,
                onValueChange = onEmailChanged,
                error = state.emailError,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            AuthPasswordField(
                label = stringResource(R.string.label_password),
                value = state.password,
                onValueChange = onPasswordChanged,
                error = state.passwordError
            )

            if (state.mode == AuthSheetMode.REGISTER) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.password_rules_hint),
                    fontSize = 11.sp,
                    color = TextoSecundario
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthPasswordField(
                    label = stringResource(R.string.label_confirm_password),
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChanged,
                    error = state.confirmPasswordError
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    if (state.mode == AuthSheetMode.LOGIN) onSubmitLogin() else onSubmitRegister()
                },
                enabled = !state.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CacaoDark,
                    contentColor = PureWhite
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        color = PureWhite,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (state.mode == AuthSheetMode.LOGIN) {
                            stringResource(R.string.btn_login)
                        } else {
                            stringResource(R.string.btn_create_account)
                        },
                        color = PureWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = {
                        onSwitchMode(
                            if (state.mode == AuthSheetMode.LOGIN) {
                                AuthSheetMode.REGISTER
                            } else {
                                AuthSheetMode.LOGIN
                            }
                        )
                    }
                ) {
                    Text(
                        text = if (state.mode == AuthSheetMode.LOGIN) {
                            stringResource(R.string.link_no_account)
                        } else {
                            stringResource(R.string.link_has_account)
                        },
                        color = CacaoDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    keyboardType: KeyboardType
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = CacaoDark,
                unfocusedBorderColor = BorderLight,
                errorBorderColor = ErrorRed
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = error, color = ErrorRed, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AuthPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    var visible by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            isError = error != null,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = null,
                        tint = TextoMuted
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = CacaoDark,
                unfocusedBorderColor = BorderLight,
                errorBorderColor = ErrorRed
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = error, color = ErrorRed, fontSize = 11.sp)
        }
    }
}
