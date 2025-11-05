package com.br.ifal.hobbyhub.models

import com.google.gson.annotations.SerializedName

data class OpenOpusComposerItem(
    val id: Int,
    val name: String,
    @SerializedName("complete_name")
    val fullName: String,
    val epoch: String
)
