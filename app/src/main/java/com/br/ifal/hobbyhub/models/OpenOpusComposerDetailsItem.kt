package com.br.ifal.hobbyhub.models

import com.google.gson.annotations.SerializedName

data class OpenOpusComposerDetailsItem(
    val id: Int,
    val name: String,
    val birth: String?,
    val death: String?,
    val epoch: String,
    @SerializedName("complete_name")
    val fullName: String,
    val portrait: String?
)
