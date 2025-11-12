package com.br.ifal.hobbyhub.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.br.ifal.hobbyhub.models.FavoriteMangaEntity

@Dao
interface MangaDao {
    @Upsert
    suspend fun insertManga(manga: FavoriteMangaEntity)
    
    @Query("SELECT * FROM favorite_manga ORDER BY title ASC")
    suspend fun getAllFavoriteMangas(): List<FavoriteMangaEntity>
    
    @Query("SELECT * FROM favorite_manga WHERE mal_id = :malId LIMIT 1")
    suspend fun getMangaByMalId(malId: Long): FavoriteMangaEntity?
    
    @Query("DELETE FROM favorite_manga WHERE mal_id = :malId")
    suspend fun deleteMangaByMalId(malId: Long)
    
    @Query("SELECT mal_id FROM favorite_manga")
    suspend fun getAllFavoriteMangaIds(): List<Long>
}
