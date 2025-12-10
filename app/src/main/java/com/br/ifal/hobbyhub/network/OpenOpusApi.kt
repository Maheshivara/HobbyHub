package com.br.ifal.hobbyhub.network

import com.br.ifal.hobbyhub.models.OpenOpusComposerDetailsResponse
import com.br.ifal.hobbyhub.models.OpenOpusWorkListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OpenOpusApi {
    @POST("dyn/work/random")
    suspend fun getRandomWorks(): Response<OpenOpusWorkListResponse>

    @GET("composer/list/ids/{idList}.json")
    suspend fun getComposersByIds(
        @Path("idList") idList: String
    ): Response<OpenOpusComposerDetailsResponse>
}