package com.br.ifal.hobbyhub.network

import com.br.ifal.hobbyhub.models.JikanMangaResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface da API Jikan (MyAnimeList)
 * Documentação: https://docs.api.jikan.moe/
 * Base URL: https://api.jikan.moe/v4/
 */
interface JikanApi {
    
    /**
     * Busca os mangás mais populares (Top Manga)
     * Endpoint: GET /top/manga
     * 
     * @param page Número da página (padrão: 1)
     * @param limit Items por página (padrão: 25, máximo: 25)
     * @param type Tipo de mangá: manga, novel, lightnovel, oneshot, doujin, manhwa, manhua
     * @param filter Filtro: publishing, upcoming, bypopularity, favorite
     * @return Lista de mangás mais populares
     */
    @GET("top/manga")
    suspend fun getTopManga(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("type") type: String? = null,
        @Query("filter") filter: String? = null
    ): JikanMangaResponse
    
    /**
     * Busca mangás por termo
     * Endpoint: GET /manga
     * 
     * @param query Termo de busca
     * @param page Número da página (padrão: 1)
     * @param limit Items por página (padrão: 25, máximo: 25)
     * @param type Tipo de mangá
     * @param status Status: publishing, complete, upcoming, discontinued
     * @param orderBy Ordenar por: title, start_date, end_date, score, rank, popularity
     * @param sort Ordem: asc, desc
     * @return Lista de mangás encontrados
     */
    @GET("manga")
    suspend fun searchManga(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("type") type: String? = null,
        @Query("status") status: String? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("sort") sort: String? = null
    ): JikanMangaResponse
    
    /**
     * Busca mangás por gênero
     * Endpoint: GET /manga
     * 
     * @param genres IDs dos gêneros separados por vírgula (ex: "1,2,4")
     * @param page Número da página
     * @param limit Items por página
     * @return Lista de mangás do(s) gênero(s)
     */
    @GET("manga")
    suspend fun getMangaByGenres(
        @Query("genres") genres: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): JikanMangaResponse
}

/**
 * Constantes úteis para a API Jikan
 */
object JikanConstants {
    
    // Tipos de Mangá
    const val TYPE_MANGA = "manga"
    const val TYPE_NOVEL = "novel"
    const val TYPE_LIGHT_NOVEL = "lightnovel"
    const val TYPE_ONESHOT = "oneshot"
    const val TYPE_DOUJIN = "doujin"
    const val TYPE_MANHWA = "manhwa"
    const val TYPE_MANHUA = "manhua"
    
    // Status
    const val STATUS_PUBLISHING = "publishing"
    const val STATUS_COMPLETE = "complete"
    const val STATUS_UPCOMING = "upcoming"
    const val STATUS_DISCONTINUED = "discontinued"
    
    // Filtros para Top Manga
    const val FILTER_PUBLISHING = "publishing"
    const val FILTER_UPCOMING = "upcoming"
    const val FILTER_BY_POPULARITY = "bypopularity"
    const val FILTER_FAVORITE = "favorite"
    
    // Ordenação
    const val ORDER_BY_TITLE = "title"
    const val ORDER_BY_START_DATE = "start_date"
    const val ORDER_BY_END_DATE = "end_date"
    const val ORDER_BY_SCORE = "score"
    const val ORDER_BY_RANK = "rank"
    const val ORDER_BY_POPULARITY = "popularity"
    
    // Ordem
    const val SORT_ASC = "asc"
    const val SORT_DESC = "desc"
    
    // Gêneros populares (IDs)
    const val GENRE_ACTION = "1"
    const val GENRE_ADVENTURE = "2"
    const val GENRE_COMEDY = "4"
    const val GENRE_DRAMA = "8"
    const val GENRE_FANTASY = "10"
    const val GENRE_HORROR = "14"
    const val GENRE_MYSTERY = "7"
    const val GENRE_ROMANCE = "22"
    const val GENRE_SCI_FI = "24"
    const val GENRE_SHOUNEN = "27"
    const val GENRE_SEINEN = "41"
    const val GENRE_SHOUJO = "25"
    
    // Rate Limits
    const val REQUESTS_PER_MINUTE = 60
    const val REQUESTS_PER_SECOND = 3
    const val MIN_REQUEST_DELAY_MS = 334L // ~3 req/sec
}
