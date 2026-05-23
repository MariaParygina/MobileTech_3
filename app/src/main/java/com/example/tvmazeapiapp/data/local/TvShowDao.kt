package com.example.tvmazeapiapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TvShowDao {
    @Query("SELECT * FROM favorite_tvshow ORDER BY name")
    suspend fun getFavorites(): List<FavoriteTvShowEntity>

    @Query("SELECT id FROM favorite_tvshow")
    suspend fun getFavoritesIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tvshow: FavoriteTvShowEntity)

    @Query("DELETE FROM favorite_tvshow WHERE id = :id")
    suspend fun deleteById(id: Int)
}

