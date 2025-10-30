package com.br.ifal.hobbyhub.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.br.ifal.hobbyhub.models.MusicAlbumEntity
import com.br.ifal.hobbyhub.models.MusicArtistEntity
import com.br.ifal.hobbyhub.models.MusicTrackEntity

@Dao
interface MusicDao {
    @Insert
    suspend fun insertArtist(artist: MusicArtistEntity)

    @Query("SELECT * FROM artist WHERE is_favorite = 1")
    suspend fun getFavoriteArtists(): List<MusicArtistEntity>

    @Query("SELECT * FROM artist WHERE deezer_id = :deezerId LIMIT 1")
    suspend fun getArtistByDeezerId(deezerId: Long): MusicArtistEntity?

    @Query("UPDATE artist SET is_favorite = :isFavorite WHERE deezer_id = :deezerId")
    suspend fun updateArtistFavoriteStatus(deezerId: Long, isFavorite: Boolean)

    @Query("DELETE FROM artist WHERE deezer_id = :deezerId")
    suspend fun deleteArtistByDeezerId(deezerId: Long)

    @Insert
    suspend fun insertAlbum(album: MusicAlbumEntity)

    @Query("SELECT * FROM album WHERE is_favorite = 1")
    suspend fun getFavoriteAlbums(): List<MusicAlbumEntity>

    @Query("SELECT * FROM album WHERE deezer_id = :deezerId LIMIT 1")
    suspend fun getAlbumByDeezerId(deezerId: Long): MusicAlbumEntity?

    @Query("UPDATE album SET is_favorite = :isFavorite WHERE deezer_id = :deezerId")
    suspend fun updateAlbumFavoriteStatus(deezerId: Long, isFavorite: Boolean)

    @Query("DELETE FROM album WHERE deezer_id = :deezerId")
    suspend fun deleteAlbumByDeezerId(deezerId: Long)

    @Insert
    suspend fun insertTrack(track: MusicTrackEntity)

    @Query("SELECT * FROM track")
    suspend fun getFavoriteTracks(): List<MusicTrackEntity>

    @Query("DELETE FROM track WHERE deezer_id = :deezerId")
    suspend fun deleteTrackByDeezerId(deezerId: Long)
}