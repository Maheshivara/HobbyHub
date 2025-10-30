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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.br.ifal.hobbyhub.models.DeezerTrackItem
import com.br.ifal.hobbyhub.models.MusicAlbumEntity
import com.br.ifal.hobbyhub.models.MusicArtistEntity
import com.br.ifal.hobbyhub.models.MusicTrackEntity
import com.br.ifal.hobbyhub.network.RetrofitProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable

fun MusicSearchScreen(navController: NavHostController) {
    val deezerApi = RetrofitProvider.deezerApi
    val musicDao = DatabaseHelper.getInstance(LocalContext.current).musicDao()
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    var favoriteTracksIds by remember { mutableStateOf<List<Long>>(emptyList()) }
    var tracks by remember { mutableStateOf<List<DeezerTrackItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        val response = withContext(Dispatchers.IO) {
            deezerApi.getChartTracks()
        }
        if (!response.isSuccessful) {
            return@LaunchedEffect
        }
        var trackList = emptyList<DeezerTrackItem>()
        var favoriteIds = emptyList<Long>()
        withContext(Dispatchers.IO) {
            val chartTracks = response.body()?.tracks?.data ?: emptyList()
            trackList = chartTracks.map { chartTrack ->
                DeezerTrackItem(
                    id = chartTrack.id,
                    rank = chartTrack.rank,
                    title = chartTrack.title,
                    duration = chartTrack.duration,
                    artist = chartTrack.artist,
                    album = chartTrack.album
                )
            }
            favoriteIds =
                musicDao.getFavoriteTracks().map { it.deezerId }
        }
        tracks = trackList
        favoriteTracksIds = favoriteIds
    }

    Scaffold(modifier = Modifier, bottomBar = { MusicBottomBar(navController) }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(tracks.size) { index ->
                val track = tracks[index]
                val isFavorite = favoriteTracksIds.contains(track.id)
                MusicInfoCard(
                    modifier = Modifier,
                    track = track,
                    isFavorite = isFavorite,
                    onFavoriteClick = { clickedTrack ->
                        coroutineScope.launch(Dispatchers.IO) {
                            if (isFavorite) {
                                musicDao.deleteTrackByDeezerId(clickedTrack.id)
                            } else {
                                var artistEntity =
                                    musicDao.getArtistByDeezerId(clickedTrack.artist.id)
                                if (artistEntity == null) {
                                    artistEntity = MusicArtistEntity(
                                        deezerId = clickedTrack.artist.id,
                                        name = clickedTrack.artist.name,
                                        picture = clickedTrack.artist.picture,
                                        fanNumber = clickedTrack.artist.fanNumber
                                    )
                                    musicDao.insertArtist(artistEntity)
                                }
                                var albumEntity = musicDao.getAlbumByDeezerId(clickedTrack.album.id)
                                if (albumEntity == null) {
                                    albumEntity = MusicAlbumEntity(
                                        deezerId = clickedTrack.album.id,
                                        title = clickedTrack.album.title,
                                        cover = clickedTrack.album.cover,
                                        tracksCount = clickedTrack.album.tracksCount,
                                        artistId = clickedTrack.artist.id
                                    )
                                    musicDao.insertAlbum(albumEntity)
                                }
                                val trackEntity = MusicTrackEntity(
                                    deezerId = clickedTrack.id,
                                    title = clickedTrack.title,
                                    duration = clickedTrack.duration,
                                    artistId = artistEntity.deezerId,
                                    albumId = albumEntity.deezerId,
                                    cover = clickedTrack.album.cover,
                                    rank = clickedTrack.rank
                                )
                                musicDao.insertTrack(trackEntity)
                            }
                            favoriteTracksIds = musicDao.getFavoriteTracks().map { it.deezerId }
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