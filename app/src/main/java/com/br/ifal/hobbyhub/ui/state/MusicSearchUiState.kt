package com.br.ifal.hobbyhub.ui.state

import com.br.ifal.hobbyhub.enums.MusicSearchScreenTypeEnum
import com.br.ifal.hobbyhub.models.DeezerTrackItem

data class MusicSearchUiState(
    val trackList: List<DeezerTrackItem> = emptyList(),
    val favoriteTracksIdList: List<Long> = emptyList(),
    val searchQuery: String = "",
    val searchPage: Int = 1,
    val totalResult: Int = 0,
    val searchType: MusicSearchScreenTypeEnum = MusicSearchScreenTypeEnum.NAME,
)