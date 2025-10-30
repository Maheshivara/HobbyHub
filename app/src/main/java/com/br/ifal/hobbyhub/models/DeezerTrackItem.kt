package com.br.ifal.hobbyhub.models


data class DeezerTrackItem(
    val id: Long,
    val title: String,
    val rank: Int,
    val duration: Int,
    val artist: DeezerArtistItem,
    val album: DeezerAlbumItem
) : DeezerSearchItem
