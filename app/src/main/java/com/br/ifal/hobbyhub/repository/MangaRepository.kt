package com.br.ifal.hobbyhub.repository

import com.br.ifal.hobbyhub.db.MangaDao
import com.br.ifal.hobbyhub.models.FavoriteMangaEntity
import com.br.ifal.hobbyhub.models.MangaItem
import com.br.ifal.hobbyhub.network.JikanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class MangaRepository(
    private val mangaDao: MangaDao,
    private val jikanApi: JikanApi
) {
    
    fun searchMangas(query: String): Flow<Result<List<MangaItem>>> = flow {
        try {
            val response = jikanApi.searchManga(query.trim())
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    fun getTopMangas(page: Int): Flow<Result<List<MangaItem>>> = flow {
        try {
            val response = jikanApi.getTopManga(page = page)
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    fun getAllFavoriteMangas(): Flow<List<FavoriteMangaEntity>> = flow {
        val favorites = mangaDao.getAllFavoriteMangas()
        emit(favorites)
    }.flowOn(Dispatchers.IO)
    
    fun getAllFavoriteMangaIds(): Flow<List<Long>> = flow {
        val ids = mangaDao.getAllFavoriteMangaIds()
        emit(ids)
    }.flowOn(Dispatchers.IO)
    
    suspend fun addToFavorites(manga: FavoriteMangaEntity) {
        withContext(Dispatchers.IO) {
            mangaDao.insertManga(manga)
        }
    }
    
    suspend fun removeFromFavorites(malId: Long) {
        withContext(Dispatchers.IO) {
            mangaDao.deleteMangaByMalId(malId)
        }
    }
    
    suspend fun isMangaFavorited(malId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            mangaDao.getMangaByMalId(malId) != null
        }
    }
}
