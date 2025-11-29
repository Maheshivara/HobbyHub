package com.br.ifal.hobbyhub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.br.ifal.hobbyhub.ui.screens.ClassicalMusicListScreen
import com.br.ifal.hobbyhub.ui.screens.FavoriteMangasScreen
import com.br.ifal.hobbyhub.ui.screens.FavoriteMusicScreen
import com.br.ifal.hobbyhub.ui.screens.GamesScreen
import com.br.ifal.hobbyhub.ui.screens.HomeScreen
import com.br.ifal.hobbyhub.ui.screens.MangaListScreen
import com.br.ifal.hobbyhub.ui.screens.MangaSearchScreen
import com.br.ifal.hobbyhub.ui.screens.MusicSearchScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController,
        startDestination = RoutesNames.HomeScreen
    ) {
        composable<RoutesNames.HomeScreen> {
            HomeScreen(navController)
        }

        composable<RoutesNames.MusicSearchScreen> {
            MusicSearchScreen(navController)
        }

        composable<RoutesNames.FavoriteMusicScreen> {
            FavoriteMusicScreen(navController)
        }

        composable<RoutesNames.ClassicalMusicListScreen> {
            ClassicalMusicListScreen(navController)
        }

        composable<RoutesNames.GamesScreen> {
            GamesScreen(navController)
        }
        composable<RoutesNames.MangaListScreen> {
            MangaListScreen(navController)
        }

        composable<RoutesNames.MangaSearchScreen> {
            MangaSearchScreen(navController)
        }

        composable<RoutesNames.FavoriteMangasScreen> {
            FavoriteMangasScreen(navController)
        }

    }

}