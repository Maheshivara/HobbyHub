package com.br.ifal.hobbyhub.models

data class OpenOpusWorkItem(
    val id: Int,
    val title: String,
    val genre: String,
    val composer: OpenOpusComposerItem
)
