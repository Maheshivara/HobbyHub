package com.br.ifal.hobbyhub.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity("artist", indices = [Index(value = ["deezer_id"], unique = true)])
data class MusicArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("deezer_id")
    val deezerId: Long,
    val name: String,
    val picture: String,
    @ColumnInfo("fan_number")
    val fanNumber: Int,
    @ColumnInfo("is_favorite")
    val isFavorite: Boolean = false
)
