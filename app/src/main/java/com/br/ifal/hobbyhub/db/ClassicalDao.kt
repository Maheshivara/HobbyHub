package com.br.ifal.hobbyhub.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.br.ifal.hobbyhub.models.ClassicalMusicEntity

@Dao
interface ClassicalDao {
    @Upsert
    suspend fun upsertMusic(music: ClassicalMusicEntity)

    @Query("SELECT * FROM classical_music")
    suspend fun getAllMusic(): List<ClassicalMusicEntity>
}