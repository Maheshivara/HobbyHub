package com.br.ifal.hobbyhub.network

import com.br.ifal.hobbyhub.models.DeezerAlbumItem
import com.br.ifal.hobbyhub.models.DeezerArtistItem
import com.br.ifal.hobbyhub.models.DeezerChartResponse
import com.br.ifal.hobbyhub.models.DeezerSearchResponse
import com.br.ifal.hobbyhub.models.DeezerTrackItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DeezerApi {

    @GET("search/artist?q=\"{artistName}\"")
    suspend fun searchArtists(
        @Path("artistName") artistName: String
    ): DeezerSearchResponse<DeezerArtistItem>

    @GET("search/album?q=\"{albumName}\"")
    suspend fun searchAlbums(
        @Path("albumName") albumName: String
    ): DeezerSearchResponse<DeezerAlbumItem>

    @GET("search/track?q=\"{trackName}\"")
    suspend fun searchTracks(
        @Path("trackName") trackName: String
    ): DeezerSearchResponse<DeezerTrackItem>

    @GET("chart")
    suspend fun getChartTracks(): Response<DeezerChartResponse>
}