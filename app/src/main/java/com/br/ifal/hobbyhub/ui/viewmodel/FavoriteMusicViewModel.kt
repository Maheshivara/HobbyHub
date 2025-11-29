package com.br.ifal.hobbyhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.ifal.hobbyhub.repositories.MusicRepository
import com.br.ifal.hobbyhub.ui.state.FavoriteMusicUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteMusicViewModel(
    private val repository: MusicRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FavoriteMusicUiState())

    val uiState get() = _state.asStateFlow()

    init {
        loadFavoriteTracks()
    }

    fun loadFavoriteTracks() {
        viewModelScope.launch {
            val favoriteTracks = repository.getFavoriteTrackInfo()
            _state.update { currentState ->
                currentState.copy(
                    favoriteTrackList = favoriteTracks
                )
            }
        }
    }

    fun removeFromFavorites(trackId: Long) {
        viewModelScope.launch {
            repository.removeFavoriteTrackById(trackId)
            loadFavoriteTracks()
        }
    }
}