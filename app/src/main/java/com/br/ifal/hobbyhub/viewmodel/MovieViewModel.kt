package com.br.ifal.hobbyhub.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br.ifal.hobbyhub.db.DatabaseHelper
import com.br.ifal.hobbyhub.models.Movie
import com.br.ifal.hobbyhub.models.MovieRating
import com.br.ifal.hobbyhub.network.RetrofitProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val movieDao = DatabaseHelper.getInstance(application).movieDao()
    private val movieApi = RetrofitProvider.movieApi

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _ratings = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val ratings: StateFlow<Map<Int, Int>> = _ratings.asStateFlow()

    init {
        fetchMovies()
        loadRatings()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            try {
                val response = movieApi.getMovies(authorization = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5MzU1ZmRmYzViMzFhN2NhZGI1NzU2NDkyNzE3YThjNSIsIm5iZiI6MTc2MzIyNDg0MC40MzkwMDAxLCJzdWIiOiI2OTE4YWQwOGJkZmUyMmE3ZDg4NzcwNmQiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.XLgZFIKg_KEbgFLpo602UN8dGTv1QBfwGL2vC4GB5FI")
                if (response.isSuccessful) {
                    _movies.value = response.body()?.results?.take(20) ?: emptyList()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun loadRatings() {
        viewModelScope.launch {
            val ratingsList = movieDao.getAllRatings()
            val ratingsMap = ratingsList.associate { it.movieId to it.rating }
            _ratings.value = ratingsMap
        }
    }

    fun updateRating(movieId: Int, rating: Int) {
        viewModelScope.launch {
            val movieRating = MovieRating(movieId, rating)
            movieDao.insertOrUpdateRating(movieRating)
            val updatedRatings = _ratings.value.toMutableMap()
            updatedRatings[movieId] = rating
            _ratings.value = updatedRatings
        }
    }
}
