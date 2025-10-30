package com.br.ifal.hobbyhub.bottombars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.br.ifal.hobbyhub.navigation.RoutesNames

enum class MusicBottomBarEntriesEnum(
    val label: String,
    val route: RoutesNames,
    val icon: ImageVector,
) {
    MusicSearchScreen(
        label = "Buscar",
        route = RoutesNames.MusicSearchScreen,
        icon = Icons.Default.Search,
    ),
    HomeScreen(
        label = "Home",
        route = RoutesNames.HomeScreen,
        icon = Icons.Default.Home,
    ),
    FavoriteMusicScreen(
        label = "Favoritas",
        route = RoutesNames.FavoriteMusicScreen,
        icon = Icons.Default.LibraryMusic,
    )
}