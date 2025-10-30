package com.br.ifal.hobbyhub.models

import com.google.gson.annotations.SerializedName

data class DeezerArtistItem(
    val id: Long,
    val name: String,
    @SerializedName("picture_medium")
    val picture: String,
    @SerializedName("nb_fan")
    val fanNumber: Int
) : DeezerSearchItem
