package com.br.ifal.hobbyhub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.br.ifal.hobbyhub.screens.ClassicalMusicListScreen
import com.br.ifal.hobbyhub.screens.FavoriteMusicScreen
import com.br.ifal.hobbyhub.screens.GamesScreen
import com.br.ifal.hobbyhub.screens.HomeScreen
import com.br.ifal.hobbyhub.screens.MusicSearchScreen

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
    }

}