package com.br.ifal.hobbyhub.ui.state

import com.br.ifal.hobbyhub.models.FavoriteMusicData

data class FavoriteMusicUiState(
    val favoriteTrackList: List<FavoriteMusicData> = emptyList()
)