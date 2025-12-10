package com.br.ifal.hobbyhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.ifal.hobbyhub.models.FavoriteMangaEntity
import com.br.ifal.hobbyhub.repository.MangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteMangasUiState(
    val favoriteMangas: List<FavoriteMangaEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FavoriteMangasViewModel(
    private val repository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteMangasUiState())
    val uiState: StateFlow<FavoriteMangasUiState> = _uiState.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                repository.getAllFavoriteMangas().collect { favorites ->
                    _uiState.update {
                        it.copy(
                            favoriteMangas = favorites,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar favoritos: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun removeFavorite(malId: Long) {
        viewModelScope.launch {
            try {
                repository.removeFromFavorites(malId)
                
                _uiState.update { currentState ->
                    currentState.copy(
                        favoriteMangas = currentState.favoriteMangas.filter { it.malId != malId }
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Erro ao remover favorito: ${e.message}")
                }
            }
        }
    }
}
