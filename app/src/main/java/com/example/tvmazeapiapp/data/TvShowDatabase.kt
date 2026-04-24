package com.example.tvmazeapiapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.tvmazeapiapp.data.local.Converters
import com.example.tvmazeapiapp.data.local.FavoriteTvShowEntity
import com.example.tvmazeapiapp.data.local.TvShowDao

@Database(
    entities = [FavoriteTvShowEntity::class],
    version = 1,
)

@TypeConverters(Converters::class)
abstract class TvShowDatabase: RoomDatabase() {
    abstract fun tvshowDao(): TvShowDao
}
