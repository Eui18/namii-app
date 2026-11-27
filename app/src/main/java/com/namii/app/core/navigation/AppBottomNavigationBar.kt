package com.namii.app.core.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.CacaoDark
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.TextoMuted

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    items: List<BottomNavTab> = BottomNavTab.items
) {
    Column {
        HorizontalDivider(color = BorderLight, thickness = 1.dp)
        NavigationBar(
            containerColor = PureWhite,
            tonalElevation = 0.dp
        ) {
            items.forEach { tab ->
                val isSelected = when (tab) {
                    BottomNavTab.Home -> currentRoute == ClientRoutes.HOME ||
                        currentRoute?.startsWith("client/establishments") == true ||
                        currentRoute == ClientRoutes.POPULAR ||
                        currentRoute == ClientRoutes.SEARCH
                    BottomNavTab.Map -> currentRoute?.startsWith(ClientRoutes.MAP_BASE) == true
                    BottomNavTab.Favorites -> currentRoute == ClientRoutes.FAVORITES
                }

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        val targetRoute = tab.baseRoute
                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                popUpTo(ClientRoutes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.labelRes),
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(tab.labelRes),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CacaoDark,
                        selectedTextColor = CacaoDark,
                        unselectedIconColor = TextoMuted,
                        unselectedTextColor = TextoMuted,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
