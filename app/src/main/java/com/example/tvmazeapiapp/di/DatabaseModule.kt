package com.example.tvmazeapiapp.di

import android.content.Context
import androidx.room.Room
import com.example.tvmazeapiapp.data.TvShowDatabase
import com.example.tvmazeapiapp.data.local.TvShowDao
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
    fun provideTvShowDatabase(
        @ApplicationContext context: Context
    ): TvShowDatabase =
        Room.databaseBuilder(
            context,
            klass = TvShowDatabase::class.java,
            name = "tvshow.db",
        ).build()

    @Provides
    @Singleton
    fun provideTvShowDao(
        database: TvShowDatabase
    ): TvShowDao = database.tvshowDao()
}