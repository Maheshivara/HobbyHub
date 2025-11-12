package com.br.ifal.hobbyhub.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.br.ifal.hobbyhub.R
import com.br.ifal.hobbyhub.navigation.RoutesNames

private data class Hobby(
    val target: RoutesNames,
    val iconResId: Int,
    val desc: String,
    val text: String
)

@Composable
fun HomeScreen(navController: NavHostController) {
    val hobbies = listOf(
        Hobby(
            RoutesNames.MusicSearchScreen,
            R.drawable.ic_music,
            "Music",
            "Música"
        ),
        Hobby(
            RoutesNames.ClassicalMusicListScreen,
            R.drawable.ic_classical_music,
            "Classical Music",
            "Musica Clássica"
        ),

        Hobby(
            RoutesNames.GamesScreen,
            R.drawable.ic_games,
            "Games",
            "Jogos"
        )
    )

    Scaffold(modifier = Modifier) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(hobbies.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (item in rowItems) {
                        Box(modifier = Modifier.weight(1f)) {
                            HobbyIcon(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                targetRoute = item.target,
                                navController = navController,
                                iconResId = item.iconResId,
                                iconDescription = item.desc,
                                iconText = item.text
                            )
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun HobbyIcon(
    targetRoute: RoutesNames,
    navController: NavHostController,
    iconResId: Int,
    iconDescription: String,
    modifier: Modifier = Modifier,
    iconText: String = "",
) {
    Card(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(iconResId),
                contentDescription = iconDescription,
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        navController.navigate(targetRoute)
                    }
            )
            Text(
                text = iconText,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }
    }
}