package com.br.ifal.hobbyhub.models

import com.google.gson.annotations.SerializedName

data class GameItem(
    val id: Int,
    val name: String,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val rating: Double,
    val genres: List<GameGenre>
)
