package com.br.ifal.hobbyhub.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "album", indices = [Index(value = ["deezer_id"], unique = true)], foreignKeys = [ForeignKey(
        entity = MusicArtistEntity::class,
        parentColumns = ["deezer_id"],
        childColumns = ["artist_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MusicAlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("deezer_id")
    val deezerId: Long,
    val title: String,
    val cover: String,
    @ColumnInfo("artist_id")
    val artistId: Long,
    @ColumnInfo("tracks_count")
    val tracksCount: Int,
    @ColumnInfo("is_favorite")
    val isFavorite: Boolean = false
)
