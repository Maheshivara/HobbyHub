package com.br.ifal.hobbyhub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.br.ifal.hobbyhub.R
import com.br.ifal.hobbyhub.bottombars.MusicBottomBar
import com.br.ifal.hobbyhub.models.FavoriteMusicData
import com.br.ifal.hobbyhub.navigation.RoutesNames
import com.br.ifal.hobbyhub.ui.viewmodel.FavoriteMusicViewModel
import com.br.ifal.hobbyhub.ui.viewmodel.MusicSearchViewModel

@Composable

fun FavoriteMusicScreen(
    onNavigateTo: (RoutesNames) -> Unit,
    favoriteViewModel: FavoriteMusicViewModel,
    musicSearchViewModel: MusicSearchViewModel
) {

    val uiState by favoriteViewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier, bottomBar = { MusicBottomBar(onNavigateTo) }) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(uiState.favoriteTrackList.size) { index ->
                val track = uiState.favoriteTrackList[index]
                MusicInfoCard(
                    modifier = Modifier,
                    track = track,
                    onRemoveClick = { trackToRemove ->
                        favoriteViewModel.removeFromFavorites(trackToRemove.deezerId) { musicSearchViewModel.reloadFavoriteTracks() }
                    }
                )
            }
        }
    }
}

@Composable
fun MusicInfoCard(
    modifier: Modifier,
    track: FavoriteMusicData,
    onRemoveClick: (FavoriteMusicData) -> Unit
) {
    Row(modifier = modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = track.coverUrl,
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
                text = track.artistName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = track.albumTitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = { onRemoveClick(track) }) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = "Remove Favorite"
            )
        }
    }
}