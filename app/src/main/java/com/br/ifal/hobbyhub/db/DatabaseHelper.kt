package com.br.ifal.hobbyhub.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.br.ifal.hobbyhub.models.ClassicalMusicEntity
import com.br.ifal.hobbyhub.models.FavoriteMangaEntity
import com.br.ifal.hobbyhub.models.MusicAlbumEntity
import com.br.ifal.hobbyhub.models.MusicArtistEntity
import com.br.ifal.hobbyhub.models.MusicTrackEntity
import com.br.ifal.hobbyhub.models.GameEntity

@Database(
    version = 2,
    entities = [
        MusicAlbumEntity::class,
        MusicTrackEntity::class,
        MusicArtistEntity::class,
        ClassicalMusicEntity::class,
        GameEntity::class
        FavoriteMangaEntity::class
    ]
)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    abstract fun classicalDao(): ClassicalDao

    abstract fun gameDao(): GameDao
    abstract fun mangaDao(): MangaDao

    companion object {
        fun getInstance(context: Context): DatabaseHelper {
            return Room.databaseBuilder(
                context,
                DatabaseHelper::class.java,
                "hobbies.db"
            )
            .fallbackToDestructiveMigration(true)
            .build()
        }
    }
}