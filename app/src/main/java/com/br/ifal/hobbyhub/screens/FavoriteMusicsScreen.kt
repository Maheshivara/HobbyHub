package com.br.ifal.hobbyhub.screens

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.br.ifal.hobbyhub.R
import com.br.ifal.hobbyhub.bottombars.MusicBottomBar
import com.br.ifal.hobbyhub.db.DatabaseHelper
import com.br.ifal.hobbyhub.models.FavoriteMusicData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable

fun FavoriteMusicScreen(navController: NavHostController) {
    val musicDao = DatabaseHelper.getInstance(LocalContext.current).musicDao()
    val coroutineScope = rememberCoroutineScope()

    var favoriteTracks by remember { mutableStateOf<List<FavoriteMusicData>>(emptyList()) }

    LaunchedEffect(Unit) {
        var tracksFromDb: List<FavoriteMusicData> = emptyList()
        withContext(Dispatchers.IO) {
            tracksFromDb = musicDao.getFavoriteTracksData()
        }
        Log.i("FavoriteMusicScreen", "Fetched ${tracksFromDb.size} favorite tracks from database.")
        favoriteTracks = tracksFromDb
    }
    Scaffold(modifier = Modifier, bottomBar = { MusicBottomBar(navController) }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(favoriteTracks.size) { index ->
                val track = favoriteTracks[index]
                MusicInfoCard(
                    modifier = Modifier,
                    track = track,
                    onFavoriteClick = { trackToRemove ->
                        coroutineScope.launch(Dispatchers.IO) {
                            musicDao.deleteTrackByDeezerId(trackToRemove.deezerId)
                            val updatedTracks = musicDao.getFavoriteTracksData()
                            favoriteTracks = updatedTracks
                        }
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
    onFavoriteClick: (FavoriteMusicData) -> Unit
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

        IconButton(onClick = { onFavoriteClick(track) }) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = "Favorite"
            )
        }
    }
}