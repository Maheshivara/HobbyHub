package com.br.ifal.hobbyhub.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.br.ifal.hobbyhub.R
import com.br.ifal.hobbyhub.bottombars.MangaBottomBar
import com.br.ifal.hobbyhub.db.DatabaseHelper
import com.br.ifal.hobbyhub.models.FavoriteMangaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FavoriteMangasScreen(navController: NavHostController) {
    val mangaDao = DatabaseHelper.getInstance(LocalContext.current).mangaDao()
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    
    var favoriteMangas by remember { mutableStateOf<List<FavoriteMangaEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        favoriteMangas = withContext(Dispatchers.IO) {
            mangaDao.getAllFavoriteMangas()
        }
    }

    Scaffold(
        modifier = Modifier,
        bottomBar = { MangaBottomBar(navController) }
    ) { innerPadding ->
        if (favoriteMangas.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nenhum mangá favoritado ainda",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteMangas.size) { index ->
                    val manga = favoriteMangas[index]
                    FavoriteMangaCard(
                        manga = manga,
                        onDeleteClick = { deletedManga ->
                            coroutineScope.launch(Dispatchers.IO) {
                                mangaDao.deleteMangaByMalId(deletedManga.malId)
                                favoriteMangas = mangaDao.getAllFavoriteMangas()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteMangaCard(
    manga: FavoriteMangaEntity,
    onDeleteClick: (FavoriteMangaEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = manga.imageUrl,
                contentDescription = manga.title,
                modifier = Modifier
                    .size(80.dp, 120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_manga),
                error = painterResource(id = R.drawable.ic_manga)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                manga.type?.let { type ->
                    Text(
                        text = type,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                manga.status?.let { status ->
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                manga.score?.let { score ->
                    Text(
                        text = "★ $score",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                val volumesChapters = buildString {
                    manga.volumes?.let { append("Volumes: $it") }
                    if (manga.volumes != null && manga.chapters != null) append(" | ")
                    manga.chapters?.let { append("Capítulos: $it") }
                }
                
                if (volumesChapters.isNotEmpty()) {
                    Text(
                        text = volumesChapters,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                manga.synopsis?.let { synopsis ->
                    Text(
                        text = synopsis,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            IconButton(onClick = { onDeleteClick(manga) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover dos favoritos",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
