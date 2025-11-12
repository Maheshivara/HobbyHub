package com.br.ifal.hobbyhub.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val backgroundImage: String?,
    val rating: Double,
    val genres: String,
    var isFavorite: Boolean = false
)
