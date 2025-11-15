package com.br.ifal.hobbyhub.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_ratings")
data class MovieRating(
    @PrimaryKey
    val movieId: Int,
    val rating: Int
)
