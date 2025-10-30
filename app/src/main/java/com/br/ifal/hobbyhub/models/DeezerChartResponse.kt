package com.br.ifal.hobbyhub.models

data class DeezerChartResponse(
    val tracks: DeezerSearchResponse<DeezerChartTrackItem>
)
