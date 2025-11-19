package com.br.ifal.hobbyhub.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.br.ifal.hobbyhub.db.DatabaseHelper
import com.br.ifal.hobbyhub.models.GameEntity
import com.br.ifal.hobbyhub.network.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GamesScreen(navController: NavHostController) {
    val api = RetrofitProvider.gameApi
    val gameDao = DatabaseHelper.getInstance(LocalContext.current).gameDao()
    val scope = rememberCoroutineScope()
    val games = remember { mutableStateListOf<GameEntity>() }

    LaunchedEffect(Unit) {
        val response = withContext(Dispatchers.IO) {
            api.getGames(apiKey = "2fb29f265ec646bf997bae29b3af397d")
        }

        if (response.isSuccessful) {
            val body = response.body()
            val newGames = body?.results?.map { game ->
                GameEntity(
                    id = game.id,
                    name = game.name,
                    backgroundImage = game.backgroundImage,
                    rating = game.rating,
                    genres = game.genres.joinToString { it.name }
                )
            } ?: emptyList()

            val favorites = withContext(Dispatchers.IO) {
                gameDao.getFavoriteGames().map { it.id }
            }

            val orderedList = newGames
                .map { it.copy(isFavorite = favorites.contains(it.id)) }
                .sortedByDescending { it.isFavorite }

            withContext(Dispatchers.Main) {
                games.clear()
                games.addAll(orderedList)
            }
        }
    }

    Scaffold(
        topBar = {
            SimpleTopBar(onBack = { navController.popBackStack() })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(games, key = { it.id }) { game ->
                GameCard(
                    game = game,
                    onToggleFavorite = { updatedGame ->
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                if (updatedGame.isFavorite)
                                    gameDao.insertGame(updatedGame)
                                else
                                    gameDao.deleteGame(updatedGame)
                            }

                            val index = games.indexOfFirst { it.id == updatedGame.id }
                            if (index != -1) {
                                games[index] = updatedGame
                                val sorted = games.sortedByDescending { it.isFavorite }
                                games.clear()
                                games.addAll(sorted)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SimpleTopBar(onBack: () -> Unit) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .statusBarsPadding()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
            Text(
                text = "Jogos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun GameCard(
    game: GameEntity,
    onToggleFavorite: (GameEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = game.backgroundImage,
                contentDescription = game.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = game.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "⭐ ${game.rating}", style = MaterialTheme.typography.bodySmall)
                Text(text = game.genres, style = MaterialTheme.typography.bodySmall)
            }

            Icon(
                imageVector = if (game.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Favoritar jogo",
                tint = if (game.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onToggleFavorite(game.copy(isFavorite = !game.isFavorite))
                    }
            )
        }
    }
}
