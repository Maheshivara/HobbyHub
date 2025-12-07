package com.br.ifal.hobbyhub.repository

import com.br.ifal.hobbyhub.db.MovieDao
import com.br.ifal.hobbyhub.models.Movie
import com.br.ifal.hobbyhub.network.MovieApi
import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieApi: MovieApi, private val movieDao: MovieDao) {

    val movies: Flow<List<Movie>> = movieDao.getMovies()

    suspend fun fetchMovies() {
        try {
            val response = movieApi.getMovies(authorization = "")
            if (response.isSuccessful) {
                response.body()?.results?.let {
                    movieDao.insertMovies(it)
                }
            }
        } catch (e: Exception) {
        }
    }
}
