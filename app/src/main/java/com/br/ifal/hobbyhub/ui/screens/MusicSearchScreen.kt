package com.br.ifal.hobbyhub.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.br.ifal.hobbyhub.R
import com.br.ifal.hobbyhub.bottombars.MusicBottomBar
import com.br.ifal.hobbyhub.enums.MusicSearchScreenTypeEnum
import com.br.ifal.hobbyhub.models.DeezerTrackItem
import com.br.ifal.hobbyhub.ui.viewmodel.MusicSearchViewModel
import com.br.ifal.hobbyhub.ui.viewmodel.MusicSearchViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun MusicSearchScreen(navController: NavHostController) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val musicViewModel: MusicSearchViewModel =
        viewModel(factory = MusicSearchViewModelFactory(context))
    val uiState by musicViewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier, bottomBar = { MusicBottomBar(navController) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            MusicSearchBar(
                query = uiState.searchQuery,
                selectedSearch = uiState.searchType,
                onChangeQuery = { musicViewModel.updateSearchQuery(it.trim()) },
                onChangeSearchType = { musicViewModel.updateSearchType(it) },
                onSearch = {
                    musicViewModel.resetSearchPage()
                    musicViewModel.searchMusic()
                }
            )
            LazyColumn(modifier = Modifier, state = listState) {
                items(uiState.trackList.size) { trackIndex ->
                    val track = uiState.trackList[trackIndex]
                    val isFavorite = uiState.favoriteTracksIdList.contains(track.id)
                    MusicInfoCard(
                        modifier = Modifier.fillMaxWidth(),
                        track = track,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            musicViewModel.toggleFavoriteTrack(track)
                        }
                    )
                }
                item {
                    PageControls(
                        onPreviousPage = {
                            musicViewModel.loadPreviousPage()
                            coroutineScope.launch { listState.scrollToItem(0) }
                        },
                        onNextPage = {
                            musicViewModel.loadNextPage()
                            coroutineScope.launch { listState.scrollToItem(0) }
                        },
                        isPreviousEnabled = uiState.searchPage > 1,
                        isNextEnabled = uiState.totalResult > uiState.searchPage * 20,
                        modifier = Modifier
                    )
                }
            }
        }

    }
}

@Composable
fun MusicInfoCard(
    modifier: Modifier,
    track: DeezerTrackItem,
    isFavorite: Boolean,
    onFavoriteClick: (DeezerTrackItem) -> Unit
) {
    Row(modifier = modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = track.album.cover,
            contentDescription = track.title,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_music),
            error = painterResource(id = R.drawable.ic_music)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = track.title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = track.artist.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = { onFavoriteClick(track) }) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite"
            )
        }
    }
}

@Composable
fun MusicSearchBar(
    query: String,
    selectedSearch: MusicSearchScreenTypeEnum,
    onChangeQuery: (String) -> Unit,
    onChangeSearchType: (MusicSearchScreenTypeEnum) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { onChangeQuery(it) },
                label = { Text("Search Music") },
                modifier = Modifier
                    .padding(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (query.trim().length >= 3) {
                            onSearch()
                        }
                    }
                )
            )
            Spacer(
                modifier = Modifier.width(5.dp)
            )
            IconButton(
                {
                    if (query.trim().length >= 3) {
                        val search = query.trim()
                        onSearch()
                    }
                },
                modifier = Modifier
                    .padding(top = 16.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        }
        Row {
            MusicSearchScreenTypeEnum.entries.forEach { screenType ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Checkbox(
                        checked = selectedSearch == screenType,
                        onCheckedChange = {
                            onChangeSearchType(screenType)
                        }
                    )
                    Text(text = screenType.displayName)
                }
            }
        }
    }
}

@Composable
fun PageControls(
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    isPreviousEnabled: Boolean,
    isNextEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { onPreviousPage() },
            enabled = isPreviousEnabled
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowLeft,
                contentDescription = "Previous Page",
                modifier.size(48.dp)
            )
        }
        IconButton(
            onClick = { onNextPage() },
            enabled = isNextEnabled
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = "Next Page",
                modifier.size(48.dp)
            )
        }
    }
}