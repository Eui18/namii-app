package com.namii.app.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namii.app.R
import com.namii.app.features.auth.presentation.viewmodels.SellerAuthViewModel
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.ErrorSurface
import com.namii.app.ui.theme.InputBackground
import com.namii.app.ui.theme.NaranjaTostado
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.SellerCacaoInk
import com.namii.app.ui.theme.SellerSoftBrown
import com.namii.app.ui.theme.TextoMuted

/**
 * V-01: Inicio de sesión del vendedor.
 */
@Composable
fun SellerLoginScreen(
    onBackToRoles: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (hasEstablishment: Boolean) -> Unit,
    viewModel: SellerAuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackToRoles) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Atrás",
                    tint = SellerCacaoInk
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.seller_login_title),
            style = MaterialTheme.typography.headlineMedium,
            color = SellerCacaoInk
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.seller_login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = SellerSoftBrown
        )

        Spacer(modifier = Modifier.height(22.dp))

        state.generalError?.let { errorMsg ->
            Surface(
                color = ErrorSurface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Text(
                    text = errorMsg,
                    color = ErrorRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Text(
            text = stringResource(R.string.label_email),
            style = MaterialTheme.typography.labelSmall,
            color = SellerSoftBrown
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChanged,
            singleLine = true,
            isError = state.emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = NaranjaTostado,
                unfocusedBorderColor = BorderLight,
                errorBorderColor = ErrorRed
            ),
            modifier = Modifier.fillMaxWidth()
        )
        state.emailError?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = it, color = ErrorRed, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.label_password),
            style = MaterialTheme.typography.labelSmall,
            color = SellerSoftBrown
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChanged,
            singleLine = true,
            isError = state.passwordError != null,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
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
                focusedBorderColor = NaranjaTostado,
                unfocusedBorderColor = BorderLight,
                errorBorderColor = ErrorRed
            ),
            modifier = Modifier.fillMaxWidth()
        )
        state.passwordError?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = it, color = ErrorRed, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = { viewModel.submitLogin(onLoginSuccess) },
            enabled = !state.isSubmitting,
            colors = ButtonDefaults.buttonColors(
                containerColor = NaranjaTostado,
                contentColor = PureWhite
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    color = PureWhite,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.btn_login),
                    color = PureWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.seller_no_account),
                color = NaranjaTostado,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
