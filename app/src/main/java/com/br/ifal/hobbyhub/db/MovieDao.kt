package com.br.ifal.hobbyhub.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.br.ifal.hobbyhub.models.Movie
import com.br.ifal.hobbyhub.models.MovieRating
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    // @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Upsert
    suspend fun insertOrUpdateRating(movieRating: MovieRating)

    @Query("SELECT * FROM movie_ratings")
    suspend fun getAllRatings(): List<MovieRating>

    @Query("SELECT * FROM movie_ratings WHERE movieId = :movieId")
    suspend fun getRatingForMovie(movieId: Int): MovieRating?

    // @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Upsert
    suspend fun insertMovies(movies: List<Movie>)

    @Query("SELECT * FROM movies")
    fun getMovies(): Flow<List<Movie>>
}
