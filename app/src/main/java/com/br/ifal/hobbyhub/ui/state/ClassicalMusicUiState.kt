package com.br.ifal.hobbyhub.ui.state

import com.br.ifal.hobbyhub.models.ClassicalMusicEntity

data class ClassicalMusicUiState(
    val workList: List<ClassicalMusicEntity> = emptyList()
)
