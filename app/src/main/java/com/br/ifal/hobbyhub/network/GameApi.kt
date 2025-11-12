package com.br.ifal.hobbyhub.network

import com.br.ifal.hobbyhub.models.GameResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GameApi {
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("page_size") pageSize: Int = 20,
    ): Response<GameResponse>
}
