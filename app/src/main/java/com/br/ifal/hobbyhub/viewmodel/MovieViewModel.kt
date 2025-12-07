package com.br.ifal.hobbyhub.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br.ifal.hobbyhub.db.DatabaseHelper
import com.br.ifal.hobbyhub.models.Movie
import com.br.ifal.hobbyhub.models.MovieRating
import com.br.ifal.hobbyhub.network.RetrofitProvider
import com.br.ifal.hobbyhub.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val movieDao = DatabaseHelper.getInstance(application).movieDao()
    private val movieApi = RetrofitProvider.movieApi
    private val movieRepository = MovieRepository(movieApi, movieDao)

    val movies: StateFlow<List<Movie>> = movieRepository.movies
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _ratings = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val ratings: StateFlow<Map<Int, Int>> = _ratings.asStateFlow()

    init {
        fetchMovies()
        loadRatings()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            movieRepository.fetchMovies()
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
