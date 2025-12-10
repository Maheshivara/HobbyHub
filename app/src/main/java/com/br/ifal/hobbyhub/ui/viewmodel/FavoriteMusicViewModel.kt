package com.br.ifal.hobbyhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.ifal.hobbyhub.models.DeezerTrackItem
import com.br.ifal.hobbyhub.repositories.MusicRepository
import com.br.ifal.hobbyhub.ui.state.FavoriteMusicUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteMusicViewModel @Inject constructor(
    private val repository: MusicRepository,
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

    fun removeFromFavorites(trackId: Long, onConcluded: () -> Unit = {}) {
        viewModelScope.launch {
            repository.removeFavoriteTrackById(trackId)
            loadFavoriteTracks()
            onConcluded()
        }
    }

    fun toggleFavoriteTrack(track: DeezerTrackItem) {
        viewModelScope.launch {
            val isFavorite = _state.value.favoriteTrackList.find {
                it.deezerId == track.id
            }?.let { true } ?: false
            if (isFavorite) {
                repository.removeFavoriteTrackById(track.id)
            } else {
                repository.addFavoriteTrack(track)
            }
            loadFavoriteTracks()
        }
    }
}