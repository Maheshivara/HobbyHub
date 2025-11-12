package com.br.ifal.hobbyhub.db

import androidx.room.*
import com.br.ifal.hobbyhub.models.GameEntity

@Dao
interface GameDao {
    @Query("SELECT * FROM games")
    suspend fun getAllGames(): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("SELECT * FROM games WHERE isFavorite = 1")
    suspend fun getFavoriteGames(): List<GameEntity>
}
