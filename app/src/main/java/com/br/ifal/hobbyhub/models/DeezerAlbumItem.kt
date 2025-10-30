package com.br.ifal.hobbyhub.models

import com.google.gson.annotations.SerializedName

data class DeezerAlbumItem(
    val id: Long,
    val title: String,
    @SerializedName("cover_medium")
    val cover: String,
    val artist: DeezerArtistItem,
    @SerializedName("nb_tracks")
    val tracksCount: Int
) : DeezerSearchItem
