package com.br.ifal.hobbyhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.ifal.hobbyhub.models.FavoriteMangaEntity
import com.br.ifal.hobbyhub.models.MangaItem
import com.br.ifal.hobbyhub.repository.MangaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MangaSearchUiState(
    val mangas: List<MangaItem> = emptyList(),
    val favoritedIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

class MangaSearchViewModel(
    private val repository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MangaSearchUiState())
    val uiState: StateFlow<MangaSearchUiState> = _uiState.asStateFlow()
    private var searchJob: Job? = null
    
    init {
        loadTopMangas()
        loadFavoritedIds()
    }
    
    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }
    
    fun searchMangas() {
        val query = _uiState.value.searchQuery.trim()
        searchJob?.cancel()
        
        if (query.isEmpty()) {
            loadTopMangas()
            return
        }
        
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(350)
            
            repository.searchMangas(query).collect { result ->
                result.fold(
                    onSuccess = { mangas ->
                        _uiState.update { 
                            it.copy(
                                mangas = mangas,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Erro ao buscar mangás: ${error.message}"
                            )
                        }
                    }
                )
            }
        }
    }
    
    private fun loadTopMangas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            repository.getTopMangas(page = 1).collect { result ->
                result.fold(
                    onSuccess = { mangas ->
                        _uiState.update {
                            it.copy(
                                mangas = mangas,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Erro ao carregar top mangás: ${error.message}"
                            )
                        }
                    }
                )
            }
        }
    }
    
    fun loadFavoritedIds() {
        viewModelScope.launch {
            repository.getAllFavoriteMangaIds().collect { ids ->
                _uiState.update { it.copy(favoritedIds = ids.toSet()) }
            }
        }
    }
    
    fun toggleFavorite(manga: MangaItem) {
        viewModelScope.launch {
            val isFavorited = _uiState.value.favoritedIds.contains(manga.malId)
            
            try {
                if (isFavorited) {
                    repository.removeFromFavorites(manga.malId)
                } else {
                    val favoriteManga = FavoriteMangaEntity(
                        malId = manga.malId,
                        title = manga.title,
                        titleEnglish = manga.titleEnglish,
                        imageUrl = manga.images.jpg.largeImageUrl,
                        type = manga.type,
                        status = manga.status,
                        chapters = manga.chapters,
                        volumes = manga.volumes,
                        score = manga.score,
                        synopsis = manga.synopsis,
                        publishedFrom = manga.published?.from,
                        publishedTo = manga.published?.to,
                        authors = null,
                        genres = null
                    )
                    repository.addToFavorites(favoriteManga)
                }
                
                loadFavoritedIds()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Erro ao atualizar favoritos: ${e.message}")
                }
            }
        }
    }
}
