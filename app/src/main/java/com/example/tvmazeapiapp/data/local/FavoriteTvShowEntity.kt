package com.example.tvmazeapiapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.TvShow

@Entity(tableName = "favorite_tvshow")
data class FavoriteTvShowEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val network: Network?,
    val genres: List<String>?,
    val rating: Rating?,
)

fun FavoriteTvShowEntity.toDomain(): TvShow = TvShow(
    id = id,
    name = name,
    network = network,
    genres = genres,
    rating = rating,
    status = null,
    premiered = null,
    ended = null,
    officialSite = null,
    summary = null,
    image = null,
    isFavorite = true,
)

fun TvShow.toFavoriteEntity(): FavoriteTvShowEntity = FavoriteTvShowEntity(
    id = id,
    name = name,
    network = network,
    genres = genres,
    rating = rating,
)