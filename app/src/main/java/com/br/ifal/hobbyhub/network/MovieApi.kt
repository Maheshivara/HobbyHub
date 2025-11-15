package com.br.ifal.hobbyhub.network

import com.br.ifal.hobbyhub.models.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MovieApi {

    @GET("discover/movie")
    suspend fun getMovies(
        @Header("Authorization") authorization: String,
        @Query("language") language: String = "pt-BR",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>
}
