package com.br.ifal.hobbyhub.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "track",
    indices = [Index(
        value = ["deezer_id"],
        unique = true
    ), Index(value = ["album_id"]), Index(value = ["artist_id"])],
    foreignKeys = [
        ForeignKey(
            entity = MusicArtistEntity::class,
            parentColumns = ["deezer_id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MusicAlbumEntity::class,
            parentColumns = ["deezer_id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MusicTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("deezer_id")
    val deezerId: Long,
    val cover: String,
    val title: String,
    val rank: Int,
    val duration: Int,
    @ColumnInfo("artist_id")
    val artistId: Long,
    @ColumnInfo("album_id")
    val albumId: Long,
)
