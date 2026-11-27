package com.namii.app.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.namii.app.features.auth.presentation.components.ClientAuthBottomSheets
import com.namii.app.features.auth.presentation.viewmodels.AuthModalViewModel
import com.namii.app.features.auth.presentation.viewmodels.PendingAuthAction
import com.namii.app.ui.theme.PureWhite
import kotlinx.coroutines.launch

@Composable
fun MainAppScreen(
    mainAppViewModel: MainAppViewModel = hiltViewModel(),
    authModalViewModel: AuthModalViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authSheetState by authModalViewModel.uiState.collectAsStateWithLifecycle()
    val pendingRating by mainAppViewModel.pendingRating.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val showMessage: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    val showBottomBar = currentRoute != ClientRoutes.DETAIL

    Scaffold(
        containerColor = PureWhite,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            pendingRating = pendingRating,
            onConsumedPendingRating = mainAppViewModel::consumePendingRating,
            onOpenLogin = {
                authModalViewModel.openLogin(
                    contextMessage = "Inicia sesión en tu cuenta",
                    pendingAction = PendingAuthAction.OpenFavorites
                )
            },
            onOpenRegister = {
                authModalViewModel.openRegister(
                    contextMessage = "Crea tu cuenta de cliente",
                    pendingAction = PendingAuthAction.OpenFavorites
                )
            },
            onRequireAuthForFavorite = { est ->
                authModalViewModel.openLogin(
                    contextMessage = "Inicia sesión para guardar ${est.name}",
                    pendingAction = PendingAuthAction.ToggleFavorite(est.id, est.name)
                )
            },
            onRequireAuthForRating = { est, stars ->
                authModalViewModel.openLogin(
                    contextMessage = "Inicia sesión para calificar ${est.name}",
                    pendingAction = PendingAuthAction.RateEstablishment(est.id, est.name, stars)
                )
            },
            onShowMessage = showMessage,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }

    ClientAuthBottomSheets(
        state = authSheetState,
        onDismiss = authModalViewModel::dismiss,
        onSwitchMode = authModalViewModel::switchMode,
        onNameChanged = authModalViewModel::onNameChanged,
        onEmailChanged = authModalViewModel::onEmailChanged,
        onPasswordChanged = authModalViewModel::onPasswordChanged,
        onConfirmPasswordChanged = authModalViewModel::onConfirmPasswordChanged,
        onSubmitLogin = {
            authModalViewModel.submitLogin { completedAction ->
                mainAppViewModel.handleAuthCompleted(completedAction, showMessage)
            }
        },
        onSubmitRegister = {
            authModalViewModel.submitRegister { completedAction ->
                mainAppViewModel.handleAuthCompleted(completedAction, showMessage)
            }
        }
    )
}
