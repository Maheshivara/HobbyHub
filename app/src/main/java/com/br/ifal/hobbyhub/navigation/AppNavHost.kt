package com.br.ifal.hobbyhub.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
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
import com.br.ifal.hobbyhub.ui.viewmodel.FavoriteMusicViewModel
import com.br.ifal.hobbyhub.ui.viewmodel.MusicSearchViewModel

@Composable
fun AppNavHost(navController: NavHostController, viewModels: Map<RoutesNames, ViewModel>) {
    NavHost(
        navController,
        startDestination = RoutesNames.HomeScreen
    ) {
        composable<RoutesNames.HomeScreen> {
            HomeScreen(navController)
        }

        composable<RoutesNames.MusicSearchScreen> {
            val viewModel = viewModels[RoutesNames.MusicSearchScreen] as MusicSearchViewModel
            MusicSearchScreen({ routeName -> navController.navigate(routeName) }, viewModel)
        }

        composable<RoutesNames.FavoriteMusicScreen> {
            val viewModel = viewModels[RoutesNames.FavoriteMusicScreen] as FavoriteMusicViewModel
            FavoriteMusicScreen({ routeName -> navController.navigate(routeName) }, viewModel)
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