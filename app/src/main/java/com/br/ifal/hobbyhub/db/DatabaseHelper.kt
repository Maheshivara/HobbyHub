package com.br.ifal.hobbyhub.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.br.ifal.hobbyhub.models.MusicAlbumEntity
import com.br.ifal.hobbyhub.models.MusicArtistEntity
import com.br.ifal.hobbyhub.models.MusicTrackEntity

@Database(
    version = 1,
    entities = [MusicAlbumEntity::class, MusicTrackEntity::class, MusicArtistEntity::class]
)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun musicDao(): MusicDao

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