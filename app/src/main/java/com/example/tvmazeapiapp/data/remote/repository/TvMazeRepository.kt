package com.example.tvmazeapiapp.data.remote.repository

import com.example.tvmazeapiapp.data.api.TvMazeApi
import com.example.tvmazeapiapp.data.local.TvShowDao
import com.example.tvmazeapiapp.data.local.toDomain
import com.example.tvmazeapiapp.data.local.toFavoriteEntity
import com.example.tvmazeapiapp.data.model.TvShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvShowRepository @Inject constructor(
    private val api: TvMazeApi,
    private val dao: TvShowDao
) {
    constructor(dao: TvShowDao) : this(
        object : TvMazeApi {
            override suspend fun getShows(page: Int): List<TvShow> = emptyList()
            override suspend fun searchShows(query: String): List<TvMazeApi.SearchResult> = emptyList()
            override suspend fun getShowById(id: Int): TvShow = TvShow(
                id = id, name = "", network = null, genres = null,
                rating = null, status = null, premiered = null,
                ended = null, officialSite = null, summary = null,
                image = null, isFavorite = false
            )
        },
        dao
    )

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

                val favoriteIds = dao.getFavoritesIds()

                val showsWithFavorites = shows.map { show ->
                    show.copy(isFavorite = favoriteIds.contains(show.id))
                }

                Result.success(showsWithFavorites)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getShowById(id: Int): TvShow {
        return api.getShowById(id)
    }

    // -------

    suspend fun getFavorites(): List<TvShow> = withContext(Dispatchers.IO) {
        dao.getFavorites().map{ it.toDomain() }
    }

    suspend fun setFavorite(show: TvShow, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        if (isFavorite) {
            dao.upsert(show.toFavoriteEntity())
        } else {
            dao.deleteById(show.id)
        }
    }

    suspend fun getFavoritesIds(): List<Int> = withContext(Dispatchers.IO) {
        dao.getFavoritesIds()
    }
}