package com.namii.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.namii.app.features.auth.presentation.screens.SellerLoginScreen
import com.namii.app.features.auth.presentation.screens.SellerRegisterScreen
import com.namii.app.features.auth.presentation.screens.WelcomeRoleScreen
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.presentation.screens.CategoryEstablishmentsScreen
import com.namii.app.features.client.establishments.presentation.screens.EstablishmentDetailScreen
import com.namii.app.features.client.establishments.presentation.screens.PopularEstablishmentsScreen
import com.namii.app.features.client.establishments.presentation.screens.SearchScreen
import com.namii.app.features.client.favorites.presentation.screens.FavoritesScreen
import com.namii.app.features.client.home.presentation.screens.ClientHomeScreen
import com.namii.app.features.client.map.presentation.screens.ClientMapScreen
import com.namii.app.features.seller.dashboard.presentation.screens.SellerDashboardScreen
import com.namii.app.features.seller.dashboard.presentation.screens.SellerEntryResolverScreen
import com.namii.app.features.seller.establishments.presentation.screens.SellerCoverAndHoursScreen
import com.namii.app.features.seller.establishments.presentation.screens.SellerEditEstablishmentScreen
import com.namii.app.features.seller.establishments.presentation.screens.SellerEditHoursScreen
import com.namii.app.features.seller.establishments.presentation.screens.SellerEditLocationScreen
import com.namii.app.features.seller.establishments.presentation.screens.SellerEstablishmentInfoScreen
import com.namii.app.features.seller.establishments.presentation.screens.SellerOnboardingLocationScreen
import com.namii.app.features.seller.establishments.presentation.viewmodels.SellerEditEstablishmentViewModel
import com.namii.app.features.seller.establishments.presentation.viewmodels.SellerOnboardingViewModel
import com.namii.app.features.seller.products.presentation.screens.SellerProductFormScreen
import com.namii.app.features.seller.products.presentation.screens.SellerProductsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    isSellerLoggedIn: () -> Boolean,
    pendingRating: Pair<Int, Int>?,
    onConsumedPendingRating: () -> Unit,
    onOpenLogin: () -> Unit,
    onOpenRegister: () -> Unit,
    onRequireAuthForFavorite: (Establishment) -> Unit,
    onRequireAuthForRating: (Establishment, stars: Int) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // V-00: Selección de rol (Quiero explorar vs Soy vendedor)
        composable(AppRoutes.WELCOME_ROLE) {
            WelcomeRoleScreen(
                onSelectExplorer = {
                    navController.navigate(ClientRoutes.HOME)
                },
                onSelectSeller = {
                    // Temporal: se omite el inicio de sesión de vendedor hasta tener la API lista
                    val target = if (isSellerLoggedIn()) {
                        SellerRoutes.ENTRY
                    } else {
                        SellerRoutes.ONBOARDING_GRAPH
                    }
                    navController.navigate(target)
                }
            )
        }

        // Flujo Cliente (Vistas 1 - 7)
        composable(ClientRoutes.HOME) {
            ClientHomeScreen(
                onOpenSearch = { navController.navigate(ClientRoutes.SEARCH) },
                onSelectCategory = { cat ->
                    navController.navigate(ClientRoutes.categoryRoute(cat))
                },
                onOpenMap = {
                    navController.navigate(ClientRoutes.MAP_BASE) {
                        popUpTo(ClientRoutes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSeeAllPopular = { navController.navigate(ClientRoutes.POPULAR) },
                onOpenDetail = { id -> navController.navigate(ClientRoutes.detailRoute(id)) },
                onRequireAuthForFavorite = onRequireAuthForFavorite,
                onShowMessage = onShowMessage
            )
        }

        composable(
            route = ClientRoutes.CATEGORY_LIST,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) {
            CategoryEstablishmentsScreen(
                onBack = { navController.popBackStack() },
                onOpenDetail = { id -> navController.navigate(ClientRoutes.detailRoute(id)) },
                onRequireAuthForFavorite = onRequireAuthForFavorite,
                onShowMessage = onShowMessage
            )
        }

        composable(ClientRoutes.POPULAR) {
            PopularEstablishmentsScreen(
                onBack = { navController.popBackStack() },
                onOpenDetail = { id -> navController.navigate(ClientRoutes.detailRoute(id)) },
                onRequireAuthForFavorite = onRequireAuthForFavorite,
                onShowMessage = onShowMessage
            )
        }

        composable(ClientRoutes.SEARCH) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onOpenDetail = { id -> navController.navigate(ClientRoutes.detailRoute(id)) },
                onRequireAuthForFavorite = onRequireAuthForFavorite,
                onShowMessage = onShowMessage
            )
        }

        composable(
            route = ClientRoutes.MAP_PATTERN,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("lng") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("businessId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            ClientMapScreen(
                onOpenDetail = { id -> navController.navigate(ClientRoutes.detailRoute(id)) },
                onRequireAuthForFavorite = onRequireAuthForFavorite,
                onShowMessage = onShowMessage
            )
        }

        composable(
            route = ClientRoutes.DETAIL,
            arguments = listOf(navArgument("businessId") { type = NavType.IntType })
        ) { backStackEntry ->
            val currentId = backStackEntry.arguments?.getInt("businessId")
            val matchingStars = if (pendingRating != null && pendingRating.first == currentId) {
                pendingRating.second
            } else {
                null
            }
            EstablishmentDetailScreen(
                onBack = { navController.popBackStack() },
                onViewInMap = { lat, lng, id ->
                    navController.navigate(ClientRoutes.mapRoute(lat, lng, id))
                },
                onRequireAuthForFavorite = onRequireAuthForFavorite,
                onRequireAuthForRating = onRequireAuthForRating,
                onShowMessage = onShowMessage,
                pendingRatingToSubmit = matchingStars,
                onConsumedPendingRating = onConsumedPendingRating
            )
        }

        composable(ClientRoutes.FAVORITES) {
            FavoritesScreen(
                onOpenDetail = { id -> navController.navigate(ClientRoutes.detailRoute(id)) },
                onOpenLogin = onOpenLogin,
                onOpenRegister = onOpenRegister,
                onShowMessage = onShowMessage
            )
        }

        // Flujo Vendedor (V-01 - V-10)
        composable(SellerRoutes.LOGIN) {
            SellerLoginScreen(
                onBackToRoles = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate(SellerRoutes.REGISTER) },
                onLoginSuccess = { hasEstablishment ->
                    val target = if (hasEstablishment) SellerRoutes.ENTRY else SellerRoutes.ONBOARDING_GRAPH
                    navController.navigate(target) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(SellerRoutes.REGISTER) {
            SellerRegisterScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(SellerRoutes.ONBOARDING_GRAPH) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(SellerRoutes.ENTRY) {
            SellerEntryResolverScreen(
                onGoToDashboard = {
                    navController.navigate(SellerRoutes.DASHBOARD) {
                        popUpTo(SellerRoutes.ENTRY) { inclusive = true }
                    }
                },
                onGoToOnboarding = {
                    navController.navigate(SellerRoutes.ONBOARDING_GRAPH) {
                        popUpTo(SellerRoutes.ENTRY) { inclusive = true }
                    }
                },
                onLoggedOut = {
                    navController.navigate(AppRoutes.WELCOME_ROLE) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        navigation(
            route = SellerRoutes.ONBOARDING_GRAPH,
            startDestination = SellerRoutes.ONBOARDING_STEP_1
        ) {
            composable(SellerRoutes.ONBOARDING_STEP_1) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(SellerRoutes.ONBOARDING_GRAPH)
                }
                val onboardingVm: SellerOnboardingViewModel = hiltViewModel(parentEntry)
                SellerEstablishmentInfoScreen(
                    viewModel = onboardingVm,
                    onBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(AppRoutes.WELCOME_ROLE) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    },
                    onNextStep = { navController.navigate(SellerRoutes.ONBOARDING_STEP_2) }
                )
            }

            composable(SellerRoutes.ONBOARDING_STEP_2) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(SellerRoutes.ONBOARDING_GRAPH)
                }
                val onboardingVm: SellerOnboardingViewModel = hiltViewModel(parentEntry)
                SellerOnboardingLocationScreen(
                    viewModel = onboardingVm,
                    onBack = { navController.popBackStack() },
                    onNextStep = { navController.navigate(SellerRoutes.ONBOARDING_STEP_3) }
                )
            }

            composable(SellerRoutes.ONBOARDING_STEP_3) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(SellerRoutes.ONBOARDING_GRAPH)
                }
                val onboardingVm: SellerOnboardingViewModel = hiltViewModel(parentEntry)
                SellerCoverAndHoursScreen(
                    viewModel = onboardingVm,
                    onBack = { navController.popBackStack() },
                    onShowMessage = onShowMessage,
                    onEstablishmentCreated = {
                        onShowMessage("¡Negocio publicado en Ñamii!")
                        navController.navigate(SellerRoutes.DASHBOARD) {
                            popUpTo(SellerRoutes.ONBOARDING_GRAPH) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(SellerRoutes.DASHBOARD) {
            SellerDashboardScreen(
                onOpenMenu = { estId -> navController.navigate(SellerRoutes.productsRoute(estId)) },
                onOpenEditEstablishment = { navController.navigate(SellerRoutes.EDIT_GRAPH) },
                onLoggedOut = {
                    navController.navigate(AppRoutes.WELCOME_ROLE) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onShowMessage = onShowMessage
            )
        }

        composable(
            route = SellerRoutes.PRODUCTS,
            arguments = listOf(navArgument("establishmentId") { type = NavType.IntType })
        ) {
            SellerProductsScreen(
                onBack = { navController.popBackStack() },
                onAddProduct = { estId -> navController.navigate(SellerRoutes.productFormRoute(estId, null)) },
                onEditProduct = { estId, prodId ->
                    navController.navigate(SellerRoutes.productFormRoute(estId, prodId))
                },
                onShowMessage = onShowMessage
            )
        }

        composable(
            route = SellerRoutes.PRODUCT_FORM,
            arguments = listOf(
                navArgument("establishmentId") { type = NavType.IntType },
                navArgument("productId") { type = NavType.IntType; defaultValue = -1 }
            )
        ) {
            SellerProductFormScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    onShowMessage("Menú actualizado")
                    navController.popBackStack()
                },
                onShowMessage = onShowMessage
            )
        }

        navigation(
            route = SellerRoutes.EDIT_GRAPH,
            startDestination = SellerRoutes.EDIT_MAIN
        ) {
            composable(SellerRoutes.EDIT_MAIN) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(SellerRoutes.EDIT_GRAPH)
                }
                val editVm: SellerEditEstablishmentViewModel = hiltViewModel(parentEntry)
                SellerEditEstablishmentScreen(
                    viewModel = editVm,
                    onBack = { navController.popBackStack() },
                    onOpenEditLocation = { navController.navigate(SellerRoutes.EDIT_LOCATION) },
                    onOpenEditHours = { navController.navigate(SellerRoutes.EDIT_HOURS) },
                    onSaved = {
                        onShowMessage("Cambios del establecimiento guardados")
                        navController.popBackStack()
                    },
                    onShowMessage = onShowMessage
                )
            }

            composable(SellerRoutes.EDIT_LOCATION) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(SellerRoutes.EDIT_GRAPH)
                }
                val editVm: SellerEditEstablishmentViewModel = hiltViewModel(parentEntry)
                SellerEditLocationScreen(
                    viewModel = editVm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(SellerRoutes.EDIT_HOURS) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(SellerRoutes.EDIT_GRAPH)
                }
                val editVm: SellerEditEstablishmentViewModel = hiltViewModel(parentEntry)
                SellerEditHoursScreen(
                    viewModel = editVm,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
