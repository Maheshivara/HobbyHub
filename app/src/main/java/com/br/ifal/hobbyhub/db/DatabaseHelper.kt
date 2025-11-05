package com.br.ifal.hobbyhub.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.br.ifal.hobbyhub.models.ClassicalMusicEntity
import com.br.ifal.hobbyhub.models.MusicAlbumEntity
import com.br.ifal.hobbyhub.models.MusicArtistEntity
import com.br.ifal.hobbyhub.models.MusicTrackEntity

@Database(
    version = 1,
    entities = [
        MusicAlbumEntity::class,
        MusicTrackEntity::class,
        MusicArtistEntity::class,
        ClassicalMusicEntity::class
    ]
)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    abstract fun classicalDao(): ClassicalDao

    companion object {
        fun getInstance(context: Context): DatabaseHelper {
            return Room.databaseBuilder(
                context,
                DatabaseHelper::class.java,
                "hobbies.db"
            ).build()
        }
    }
}