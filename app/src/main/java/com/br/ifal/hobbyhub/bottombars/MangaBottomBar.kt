package com.br.ifal.hobbyhub.bottombars

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MangaBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    BottomAppBar {
        MangaBottomBarEntriesEnum.entries.map { bottomNavigationItem ->
            val isSelected =
                currentDestination?.hierarchy?.any { it.hasRoute(bottomNavigationItem.route::class) } == true

            if (currentDestination != null) {
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        navController.navigate(bottomNavigationItem.route)
                    },
                    icon = {
                        Icon(
                            imageVector = bottomNavigationItem.icon,
                            contentDescription = bottomNavigationItem.label
                        )
                    },
                    alwaysShowLabel = isSelected,
                    label = {
                        Text(bottomNavigationItem.label)
                    }
                )
            }
        }
    }
}
