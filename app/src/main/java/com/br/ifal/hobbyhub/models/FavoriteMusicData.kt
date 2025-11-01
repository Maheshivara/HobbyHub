package com.br.ifal.hobbyhub.models

import androidx.room.ColumnInfo

data class FavoriteMusicData(
    @ColumnInfo("deezer_id")
    val deezerId: Long,
    val title: String,
    @ColumnInfo("artist_name")
    val artistName: String,
    @ColumnInfo("album_title")
    val albumTitle: String,
    @ColumnInfo("cover")
    val coverUrl: String
)
