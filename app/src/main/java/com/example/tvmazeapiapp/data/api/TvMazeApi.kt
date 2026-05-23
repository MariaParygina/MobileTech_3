package com.example.tvmazeapiapp.data.api

import com.example.tvmazeapiapp.data.model.TvShow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {
    @GET("shows")
    suspend fun getShows(@Query("page") page: Int = 0): List<TvShow>

    @GET("search/shows")
    suspend fun searchShows(@Query("q") query: String): List<SearchResult>

    data class SearchResult(
        val score: Double,
        val show: TvShow
    )

    @GET("shows/{id}")
    suspend fun getShowById(@Path("id") id: Int): TvShow
}