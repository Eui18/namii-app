package com.namii.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.namii.app.features.client.establishments.domain.entities.Establishment
import com.namii.app.features.client.establishments.presentation.screens.CategoryEstablishmentsScreen
import com.namii.app.features.client.establishments.presentation.screens.EstablishmentDetailScreen
import com.namii.app.features.client.establishments.presentation.screens.PopularEstablishmentsScreen
import com.namii.app.features.client.establishments.presentation.screens.SearchScreen
import com.namii.app.features.client.favorites.presentation.screens.FavoritesScreen
import com.namii.app.features.client.home.presentation.screens.ClientHomeScreen
import com.namii.app.features.client.map.presentation.screens.ClientMapScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
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
        startDestination = ClientRoutes.HOME,
        modifier = modifier
    ) {
        composable(ClientRoutes.HOME) {
            ClientHomeScreen(
                onOpenSearch = { navController.navigate(ClientRoutes.SEARCH) },
                onSelectCategory = { cat ->
                    navController.navigate(ClientRoutes.categoryRoute(cat))
                },
                onOpenMap = {
                    navController.navigate(ClientRoutes.MAP_BASE) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
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
    }
}
