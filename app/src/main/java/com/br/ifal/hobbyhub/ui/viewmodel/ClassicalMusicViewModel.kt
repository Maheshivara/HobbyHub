package com.br.ifal.hobbyhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.ifal.hobbyhub.models.ClassicalMusicEntity
import com.br.ifal.hobbyhub.repositories.ClassicalRepository
import com.br.ifal.hobbyhub.ui.state.ClassicalMusicUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassicalMusicViewModel @Inject constructor(
    private val classicalRepository: ClassicalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ClassicalMusicUiState())

    val uiState get() = _uiState.asStateFlow()

    init {
        loadRandomWorks()
    }

    fun loadRandomWorks() {
        viewModelScope.launch {
            try {
                val works = classicalRepository.getWorks()
                _uiState.update {
                    it.copy(workList = works)
                }
            } catch (_: Exception) {

            }
        }
    }

    fun upsertWork(work: ClassicalMusicEntity) {
        viewModelScope.launch {
            classicalRepository.upsertWork(work)
            val updatedWorks = _uiState.value.workList.map {
                if (it.id == work.id) work else it
            }.sortedByDescending {
                it.rating
            }
            _uiState.update {
                it.copy(workList = updatedWorks)
            }
        }
    }
}