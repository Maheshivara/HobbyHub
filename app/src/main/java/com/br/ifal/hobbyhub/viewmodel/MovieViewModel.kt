package com.br.ifal.hobbyhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.ifal.hobbyhub.models.Movie
import com.br.ifal.hobbyhub.network.RetrofitProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val movieApi = RetrofitProvider.movieApi

    init {
        fetchMovies()
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
}
