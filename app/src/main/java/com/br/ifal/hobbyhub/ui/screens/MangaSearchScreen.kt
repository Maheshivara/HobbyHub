package com.br.ifal.hobbyhub.ui.screens

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.br.ifal.hobbyhub.R
import com.br.ifal.hobbyhub.bottombars.MangaBottomBar
import com.br.ifal.hobbyhub.db.DatabaseHelper
import com.br.ifal.hobbyhub.models.FavoriteMangaEntity
import com.br.ifal.hobbyhub.models.MangaItem
import com.br.ifal.hobbyhub.network.RetrofitProvider
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MangaSearchScreen(navController: NavHostController) {
    val jikanApi = RetrofitProvider.jikanApi
    val mangaDao = DatabaseHelper.getInstance(LocalContext.current).mangaDao()
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    var mangas by remember { mutableStateOf<List<MangaItem>>(emptyList()) }
    var favoriteMangaIds by remember { mutableStateOf<List<Long>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        favoriteMangaIds = withContext(Dispatchers.IO) {
            mangaDao.getAllFavoriteMangaIds()
        }
    }

    Scaffold(
        modifier = Modifier,
        bottomBar = { MangaBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            MangaSearchBar(
                query = searchQuery,
                onQueryChange = { newQuery -> searchQuery = newQuery },
                onSearch = { query ->
                    if (query.trim().length >= 3) {
                        coroutineScope.launch {
                            isLoading = true
                            withContext(Dispatchers.IO) {
                                try {
                                    delay(350)
                                    val response = jikanApi.searchManga(query.trim())
                                    mangas = response.data
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            isLoading = false
                        }
                    }
                }
            )

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mangas.size) { index ->
                        val manga = mangas[index]
                        val isFavorite = favoriteMangaIds.contains(manga.malId)
                        MangaSearchCard(
                            manga = manga,
                            isFavorite = isFavorite,
                            onFavoriteClick = { clickedManga ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    if (isFavorite) {
                                        mangaDao.deleteMangaByMalId(clickedManga.malId)
                                    } else {
                                        val gson = Gson()
                                        val mangaEntity = FavoriteMangaEntity(
                                            malId = clickedManga.malId,
                                            title = clickedManga.title,
                                            titleEnglish = clickedManga.titleEnglish,
                                            imageUrl = clickedManga.images.jpg.largeImageUrl,
                                            type = clickedManga.type,
                                            chapters = clickedManga.chapters,
                                            volumes = clickedManga.volumes,
                                            status = clickedManga.status,
                                            publishedFrom = clickedManga.published?.from,
                                            publishedTo = clickedManga.published?.to,
                                            score = clickedManga.score,
                                            synopsis = clickedManga.synopsis,
                                            authors = gson.toJson(clickedManga.authors),
                                            genres = gson.toJson(clickedManga.genres)
                                        )
                                        mangaDao.insertManga(mangaEntity)
                                    }
                                    favoriteMangaIds = mangaDao.getAllFavoriteMangaIds()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MangaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Buscar Mangá") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (query.trim().length >= 3) {
                        onSearch(query)
                    }
                }
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (query.trim().length >= 3) {
                    onSearch(query)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        }
    }
}

@Composable
fun MangaSearchCard(
    manga: MangaItem,
    isFavorite: Boolean,
    onFavoriteClick: (MangaItem) -> Unit
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
                model = manga.images.jpg.largeImageUrl,
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

                Text(
                    text = manga.type ?: "Desconhecido",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                manga.score?.let { score ->
                    Text(
                        text = "★ $score",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
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

            IconButton(onClick = { onFavoriteClick(manga) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favoritar",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
