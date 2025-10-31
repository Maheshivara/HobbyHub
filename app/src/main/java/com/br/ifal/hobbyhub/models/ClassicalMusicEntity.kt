package com.br.ifal.hobbyhub.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classical_music")
data class ClassicalMusicEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    @ColumnInfo(name = "composer_name")
    val composerName: String,
    val genre: String,
    @ColumnInfo(name = "composer_picture")
    val composerPicture: String?,
    var rating: Int = 0
)
