package com.example.tvmazeapiapp.data.repository

import com.example.tvmazeapiapp.data.api.TvMazeApi
import com.example.tvmazeapiapp.data.model.TvShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TvShowRepository(
    private val api: TvMazeApi
) {
    suspend fun getShows(page: Int = 0): Result<List<TvShow>> {
        return withContext(Dispatchers.IO) {
            try {
                val shows = api.getShows(page)
                Result.success(shows)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun searchShows(query: String): Result<List<TvShow>> {
        return withContext(Dispatchers.IO) {
            try {
                val results = api.searchShows(query)
                val shows = results.map { it.show }
                Result.success(shows)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getShowById(id: Int): TvShow {
        return api.getShowById(id)
    }
}