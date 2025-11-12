package com.br.ifal.hobbyhub.bottombars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.br.ifal.hobbyhub.navigation.RoutesNames

enum class MangaBottomBarEntriesEnum(
    val label: String,
    val route: RoutesNames,
    val icon: ImageVector,
) {
    MangaListScreen(
        label = "Top Mangás",
        route = RoutesNames.MangaListScreen,
        icon = Icons.Default.TrendingUp,
    ),
    MangaSearchScreen(
        label = "Buscar",
        route = RoutesNames.MangaSearchScreen,
        icon = Icons.Default.Search,
    ),
    FavoriteMangasScreen(
        label = "Favoritos",
        route = RoutesNames.FavoriteMangasScreen,
        icon = Icons.Default.Favorite,
    )
}
