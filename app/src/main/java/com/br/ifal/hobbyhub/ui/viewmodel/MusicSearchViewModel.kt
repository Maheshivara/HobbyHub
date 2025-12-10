package com.br.ifal.hobbyhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.ifal.hobbyhub.enums.MusicSearchScreenTypeEnum
import com.br.ifal.hobbyhub.models.DeezerTrackItem
import com.br.ifal.hobbyhub.repositories.MusicRepository
import com.br.ifal.hobbyhub.ui.state.MusicSearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSearchViewModel @Inject constructor(
    private val repository: MusicRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(MusicSearchUiState())
    val uiState get() = _state.asStateFlow()

    init {
        getTopTracks()
    }

    private fun getTopTracks() {
        viewModelScope.launch {
            val topTracksResponse = repository.fetchTopTracks()
            if (topTracksResponse.isSuccessful) {
                val tracks = topTracksResponse.body()?.tracks
                if (tracks != null) {
                    _state.update { currentState ->
                        currentState.copy(
                            trackList = tracks.data.map {
                                DeezerTrackItem(
                                    id = it.id,
                                    rank = it.rank,
                                    title = it.title,
                                    duration = it.duration,
                                    artist = it.artist,
                                    album = it.album
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update { currentState ->
            currentState.copy(searchQuery = query)
        }
    }

    fun updateSearchType(type: MusicSearchScreenTypeEnum) {
        _state.update { currentState ->
            currentState.copy(searchType = type)
        }
    }

    fun searchMusic() {
        viewModelScope.launch {
            val query = _state.value.searchQuery
            val type = _state.value.searchType
            val page = _state.value.searchPage
            val searchResponse = repository.searchTracks(query, type.apiValue, page)
            if (searchResponse.isSuccessful) {
                val tracks = searchResponse.body()?.data
                val total = searchResponse.body()?.total ?: 0
                if (tracks != null) {
                    val newTracksList = if (page == 1) {
                        tracks
                    } else {
                        _state.value.trackList + tracks.map {
                            DeezerTrackItem(
                                id = it.id,
                                rank = it.rank,
                                title = it.title,
                                duration = it.duration,
                                artist = it.artist,
                                album = it.album
                            )
                        }
                    }
                    _state.update { currentState ->
                        currentState.copy(
                            trackList = newTracksList,
                            totalResult = total
                        )
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            val currentPage = _state.value.searchPage
            val totalResults = _state.value.totalResult
            if (currentPage * 20 >= totalResults) {
                return@launch
            }
            _state.update { currentState ->
                currentState.copy(searchPage = currentPage + 1)
            }
            searchMusic()
        }
    }

    fun resetSearchPage() {
        _state.update { currentState ->
            currentState.copy(
                searchPage = 1,
                totalResult = 0
            )
        }
    }
}