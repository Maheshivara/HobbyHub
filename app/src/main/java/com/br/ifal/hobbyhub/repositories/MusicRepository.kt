package com.br.ifal.hobbyhub.repositories

import com.br.ifal.hobbyhub.db.MusicDao
import com.br.ifal.hobbyhub.models.DeezerTrackItem
import com.br.ifal.hobbyhub.models.MusicAlbumEntity
import com.br.ifal.hobbyhub.models.MusicArtistEntity
import com.br.ifal.hobbyhub.models.MusicTrackEntity
import com.br.ifal.hobbyhub.network.DeezerApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val musicDao: MusicDao,
    private val deezerApi: DeezerApi,
) {
    suspend fun getFavoriteTrackInfo() = musicDao.getFavoriteTracksData()

    suspend fun getFavoriteTrackIdList() = musicDao.getFavoriteTracks().map { it.deezerId }

    suspend fun removeFavoriteTrackById(deezerId: Long) =
        musicDao.deleteTrackByDeezerId(deezerId)

    suspend fun addFavoriteTrack(track: DeezerTrackItem) {
        val savedArtist = musicDao.getArtistByDeezerId(track.artist.id)
        val savedAlbum = musicDao.getAlbumByDeezerId(track.album.id)
        if (savedArtist == null) {
            val artistEntity = MusicArtistEntity(
                deezerId = track.artist.id,
                name = track.artist.name,
                picture = track.artist.picture,
                fanNumber = track.artist.fanNumber
            )
            musicDao.insertArtist(artistEntity)
        }
        if (savedAlbum == null) {
            val albumEntity = MusicAlbumEntity(
                deezerId = track.album.id,
                title = track.album.title,
                cover = track.album.cover,
                tracksCount = track.album.tracksCount,
                artistId = track.artist.id
            )
            musicDao.insertAlbum(albumEntity)
        }
        val trackEntity = MusicTrackEntity(
            deezerId = track.id,
            title = track.title,
            duration = track.duration,
            rank = track.rank,
            artistId = track.artist.id,
            albumId = track.album.id,
            cover = track.album.cover
        )
        musicDao.insertTrack(trackEntity)
    }

    suspend fun fetchTopTracks() = deezerApi.getChartTracks()

    suspend fun searchTracks(query: String, type: String, page: Int) =
        deezerApi.searchTracks("$type:\"$query\"", index = (page - 1) * 20)
}
