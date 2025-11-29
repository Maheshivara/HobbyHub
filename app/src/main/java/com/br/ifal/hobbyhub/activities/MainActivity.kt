package com.br.ifal.hobbyhub.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.br.ifal.hobbyhub.navigation.AppNavHost
import com.br.ifal.hobbyhub.navigation.RoutesNames
import com.br.ifal.hobbyhub.ui.theme.HobbyHubTheme
import com.br.ifal.hobbyhub.ui.viewmodel.FavoriteMusicViewModel
import com.br.ifal.hobbyhub.ui.viewmodel.MusicSearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HobbyHubTheme {
                val viewModelsMap = mapOf(
                    RoutesNames.MusicSearchScreen to hiltViewModel<MusicSearchViewModel>(),
                    RoutesNames.FavoriteMusicScreen to hiltViewModel<FavoriteMusicViewModel>()
                )
                val navController = rememberNavController()
                AppNavHost(navController, viewModelsMap)
            }
        }
    }
}