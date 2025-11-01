package com.br.ifal.hobbyhub.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.br.ifal.hobbyhub.models.FavoriteMusicData
import com.br.ifal.hobbyhub.models.MusicAlbumEntity
import com.br.ifal.hobbyhub.models.MusicArtistEntity
import com.br.ifal.hobbyhub.models.MusicTrackEntity

@Dao
interface MusicDao {
    @Insert
    suspend fun insertArtist(artist: MusicArtistEntity)

    @Query("SELECT * FROM artist WHERE deezer_id = :deezerId LIMIT 1")
    suspend fun getArtistByDeezerId(deezerId: Long): MusicArtistEntity?

    @Insert
    suspend fun insertAlbum(album: MusicAlbumEntity)

    @Query("SELECT * FROM album WHERE deezer_id = :deezerId LIMIT 1")
    suspend fun getAlbumByDeezerId(deezerId: Long): MusicAlbumEntity?

    @Insert
    suspend fun insertTrack(track: MusicTrackEntity)

    @Query("SELECT * FROM track")
    suspend fun getFavoriteTracks(): List<MusicTrackEntity>

    @Query("SELECT track.deezer_id as deezer_id, track.title as title,track.cover as cover, artist.name as artist_name, album.title as album_title FROM track JOIN album ON track.album_id = album.deezer_id JOIN artist ON track.artist_id = artist.deezer_id")
    suspend fun getFavoriteTracksData(): List<FavoriteMusicData>

    @Query("DELETE FROM track WHERE deezer_id = :deezerId")
    suspend fun deleteTrackByDeezerId(deezerId: Long)
}