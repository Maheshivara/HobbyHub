package com.br.ifal.hobbyhub.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_manga",
    indices = [Index(value = ["mal_id"], unique = true)]
)
data class FavoriteMangaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    @ColumnInfo(name = "mal_id")
    val malId: Long,
    
    val title: String,
    
    @ColumnInfo(name = "title_english")
    val titleEnglish: String?,
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    
    val type: String?,
    
    val status: String?,
    
    val chapters: Int?,
    
    val volumes: Int?,
    
    val score: Double?,
    
    val synopsis: String?,
    
    @ColumnInfo(name = "published_from")
    val publishedFrom: String?,
    
    @ColumnInfo(name = "published_to")
    val publishedTo: String?,
    
    val authors: String?,
    
    val genres: String?
)
