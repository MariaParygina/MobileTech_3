package com.example.tvmazeapiapp.data.model

import com.google.gson.annotations.SerializedName

data class TvShow(
    val id: Int,
    val name: String,
    val network: Network?,
    val genres: List<String>?,
    val rating: Rating?,

    val status: String?,
    val premiered: String?,
    val ended: String?,
    val officialSite: String?,
    val summary: String?,
    val image: Image?
)

data class Rating(
    val average: Double?
)

data class Network(
    val country: Country?,
    @SerializedName("name")
    val holder: String
)

data class Country(
    val name: String
)

data class Image(
    val medium: String?
)