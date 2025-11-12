package com.br.ifal.hobbyhub.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val deezerApi: DeezerApi by lazy {
        createRetrofit("https://api.deezer.com/")
            .create(DeezerApi::class.java)
    }

    val openOpusApi: OpenOpusApi by lazy {
        createRetrofit("https://api.openopus.org/")
            .create(OpenOpusApi::class.java)
    }

    val gameApi: GameApi by lazy {
        createRetrofit("https://api.rawg.io/api/")
            .create(GameApi::class.java)
    }
}