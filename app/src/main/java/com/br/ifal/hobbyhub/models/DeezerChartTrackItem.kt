package com.br.ifal.hobbyhub.models


data class DeezerChartTrackItem(
    val id: Long,
    val title: String,
    val rank: Int,
    val position: Int,
    val duration: Int,
    val artist: DeezerArtistItem,
    val album: DeezerAlbumItem
) : DeezerSearchItem
