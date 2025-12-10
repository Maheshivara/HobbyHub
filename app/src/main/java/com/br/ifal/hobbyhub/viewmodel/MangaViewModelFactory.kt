package com.br.ifal.hobbyhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.br.ifal.hobbyhub.repository.MangaRepository

class MangaViewModelFactory(
    private val repository: MangaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MangaSearchViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                MangaSearchViewModel(repository) as T
            }
            
            modelClass.isAssignableFrom(FavoriteMangasViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                FavoriteMangasViewModel(repository) as T
            }
            
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
