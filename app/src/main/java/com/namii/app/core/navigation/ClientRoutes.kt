package com.namii.app.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import com.namii.app.R
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory

sealed class BottomNavTab(
    val baseRoute: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    data object Home : BottomNavTab(
        baseRoute = ClientRoutes.HOME,
        labelRes = R.string.nav_home,
        icon = Icons.Rounded.Home
    )

    data object Map : BottomNavTab(
        baseRoute = ClientRoutes.MAP_BASE,
        labelRes = R.string.nav_map,
        icon = Icons.Rounded.LocationOn
    )

    data object Favorites : BottomNavTab(
        baseRoute = ClientRoutes.FAVORITES,
        labelRes = R.string.nav_favorites,
        icon = Icons.Rounded.Favorite
    )

    companion object {
        val items = listOf(Home, Map, Favorites)
    }
}

object ClientRoutes {
    const val HOME = "client/home"
    const val CATEGORY_LIST = "client/establishments/{categoryName}"
    const val POPULAR = "client/popular"
    const val SEARCH = "client/search"
    const val MAP_BASE = "client/map"
    const val MAP_PATTERN = "client/map?lat={lat}&lng={lng}&businessId={businessId}"
    const val DETAIL = "client/establishment/{businessId}"
    const val FAVORITES = "client/favorites"

    fun categoryRoute(category: EstablishmentCategory?): String {
        val key = category?.name ?: "ALL"
        return "client/establishments/$key"
    }

    fun mapRoute(lat: Double? = null, lng: Double? = null, businessId: Int? = null): String {
        return if (lat != null && lng != null && businessId != null) {
            "client/map?lat=$lat&lng=$lng&businessId=$businessId"
        } else {
            MAP_BASE
        }
    }

    fun detailRoute(businessId: Int): String = "client/establishment/$businessId"
}

object AppRoutes {
    const val WELCOME_ROLE = "welcome/role"
}

object SellerRoutes {
    const val LOGIN = "seller/login"
    const val REGISTER = "seller/register"
    const val ENTRY = "seller/entry"

    const val ONBOARDING_GRAPH = "seller/onboarding"
    const val ONBOARDING_STEP_1 = "seller/onboarding/info"
    const val ONBOARDING_STEP_2 = "seller/onboarding/location"
    const val ONBOARDING_STEP_3 = "seller/onboarding/cover_hours"

    const val DASHBOARD = "seller/dashboard"
    const val PRODUCTS = "seller/products/{establishmentId}"
    const val PRODUCT_FORM = "seller/products/{establishmentId}/form?productId={productId}"

    const val EDIT_GRAPH = "seller/edit"
    const val EDIT_MAIN = "seller/edit/main"
    const val EDIT_LOCATION = "seller/edit/location"
    const val EDIT_HOURS = "seller/edit/hours"

    fun productsRoute(establishmentId: Int): String = "seller/products/$establishmentId"

    fun productFormRoute(establishmentId: Int, productId: Int? = null): String {
        val pid = productId ?: -1
        return "seller/products/$establishmentId/form?productId=$pid"
    }
}
