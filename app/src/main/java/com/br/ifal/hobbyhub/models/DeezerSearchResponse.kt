package com.br.ifal.hobbyhub.models

data class DeezerSearchResponse<T : DeezerSearchItem>(
    val data: List<T>,
    val total: Int
)
