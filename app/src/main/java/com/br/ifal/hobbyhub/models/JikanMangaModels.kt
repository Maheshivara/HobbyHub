package com.br.ifal.hobbyhub.models

import com.google.gson.annotations.SerializedName

// Resposta principal da API
data class JikanMangaResponse(
    @SerializedName("pagination")
    val pagination: JikanPagination,
    
    @SerializedName("data")
    val data: List<MangaItem>
)

// Paginação
data class JikanPagination(
    @SerializedName("last_visible_page")
    val lastVisiblePage: Int,
    
    @SerializedName("has_next_page")
    val hasNextPage: Boolean,
    
    @SerializedName("current_page")
    val currentPage: Int,
    
    @SerializedName("items")
    val items: JikanPaginationItems
)

data class JikanPaginationItems(
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("per_page")
    val perPage: Int
)

// Item de Mangá (principal)
data class MangaItem(
    @SerializedName("mal_id")
    val malId: Long,
    
    @SerializedName("url")
    val url: String,
    
    @SerializedName("images")
    val images: MangaImages,
    
    @SerializedName("approved")
    val approved: Boolean,
    
    @SerializedName("titles")
    val titles: List<MangaTitle>,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("title_english")
    val titleEnglish: String?,
    
    @SerializedName("title_japanese")
    val titleJapanese: String?,
    
    @SerializedName("type")
    val type: String?, // Manga, Novel, Light Novel, etc.
    
    @SerializedName("chapters")
    val chapters: Int?,
    
    @SerializedName("volumes")
    val volumes: Int?,
    
    @SerializedName("status")
    val status: String?, // Finished, Publishing, etc.
    
    @SerializedName("publishing")
    val publishing: Boolean,
    
    @SerializedName("published")
    val published: MangaPublished?,
    
    @SerializedName("score")
    val score: Double?,
    
    @SerializedName("scored_by")
    val scoredBy: Int?,
    
    @SerializedName("rank")
    val rank: Int?,
    
    @SerializedName("popularity")
    val popularity: Int?,
    
    @SerializedName("members")
    val members: Int?,
    
    @SerializedName("favorites")
    val favorites: Int?,
    
    @SerializedName("synopsis")
    val synopsis: String?,
    
    @SerializedName("background")
    val background: String?,
    
    @SerializedName("authors")
    val authors: List<MangaAuthor>?,
    
    @SerializedName("serializations")
    val serializations: List<MangaSerialization>?,
    
    @SerializedName("genres")
    val genres: List<MangaGenre>?,
    
    @SerializedName("themes")
    val themes: List<MangaTheme>?,
    
    @SerializedName("demographics")
    val demographics: List<MangaDemographic>?
)

// Imagens
data class MangaImages(
    @SerializedName("jpg")
    val jpg: MangaImageFormat,
    
    @SerializedName("webp")
    val webp: MangaImageFormat?
)

data class MangaImageFormat(
    @SerializedName("image_url")
    val imageUrl: String,
    
    @SerializedName("small_image_url")
    val smallImageUrl: String,
    
    @SerializedName("large_image_url")
    val largeImageUrl: String
)

// Títulos
data class MangaTitle(
    @SerializedName("type")
    val type: String,
    
    @SerializedName("title")
    val title: String
)

// Publicação
data class MangaPublished(
    @SerializedName("from")
    val from: String?,
    
    @SerializedName("to")
    val to: String?,
    
    @SerializedName("string")
    val string: String?
)

// Autor
data class MangaAuthor(
    @SerializedName("mal_id")
    val malId: Long,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("url")
    val url: String
)

// Serialização (Revista/Magazine)
data class MangaSerialization(
    @SerializedName("mal_id")
    val malId: Long,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("url")
    val url: String
)

// Gênero
data class MangaGenre(
    @SerializedName("mal_id")
    val malId: Long,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("url")
    val url: String
)

// Tema
data class MangaTheme(
    @SerializedName("mal_id")
    val malId: Long,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("url")
    val url: String
)

// Demografia
data class MangaDemographic(
    @SerializedName("mal_id")
    val malId: Long,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("name")
    val name: String, // Shounen, Seinen, Shoujo, Josei
    
    @SerializedName("url")
    val url: String
)
