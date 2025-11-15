package com.br.ifal.hobbyhub.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.br.ifal.hobbyhub.models.MovieRating

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRating(movieRating: MovieRating)

    @Query("SELECT * FROM movie_ratings")
    suspend fun getAllRatings(): List<MovieRating>

    @Query("SELECT * FROM movie_ratings WHERE movieId = :movieId")
    suspend fun getRatingForMovie(movieId: Int): MovieRating?
}
