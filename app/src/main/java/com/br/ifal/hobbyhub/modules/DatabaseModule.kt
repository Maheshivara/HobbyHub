package com.br.ifal.hobbyhub.modules

import android.content.Context
import com.br.ifal.hobbyhub.db.ClassicalDao
import com.br.ifal.hobbyhub.db.DatabaseHelper
import com.br.ifal.hobbyhub.db.MusicDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DatabaseHelper {
        return DatabaseHelper.getInstance(context)
    }

    @Provides
    fun provideMusicDao(db: DatabaseHelper): MusicDao = db.musicDao()

    @Provides
    fun provideClassicalDao(db: DatabaseHelper): ClassicalDao = db.classicalDao()
}
