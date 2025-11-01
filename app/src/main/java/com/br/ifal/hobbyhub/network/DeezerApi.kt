package com.br.ifal.hobbyhub.network

import com.br.ifal.hobbyhub.models.DeezerChartResponse
import com.br.ifal.hobbyhub.models.DeezerSearchResponse
import com.br.ifal.hobbyhub.models.DeezerTrackItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApi {
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String
    ): Response<DeezerSearchResponse<DeezerTrackItem>>

    @GET("chart")
    suspend fun getChartTracks(): Response<DeezerChartResponse>
}