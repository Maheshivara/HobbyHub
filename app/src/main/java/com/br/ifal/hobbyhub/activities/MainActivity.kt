package com.br.ifal.hobbyhub.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.br.ifal.hobbyhub.navigation.AppNavHost
import com.br.ifal.hobbyhub.ui.theme.HobbyHubTheme
import com.br.ifal.hobbyhub.ui.viewmodel.ViewModelsProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HobbyHubTheme {
                val viewModelProvider = ViewModelsProvider(
                    musicSearchViewModel = hiltViewModel(),
                    favoriteMusicViewModel = hiltViewModel(),
                    classicalMusicViewModel = hiltViewModel()
                )
                val navController = rememberNavController()
                AppNavHost(navController, viewModelProvider)
            }
        }
    }
}