package com.example.tvmazeapiapp.data.local

import androidx.room.TypeConverter
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    // Конвертер для List<String>
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

    // Конвертер для Network (с полем holder)
    @TypeConverter
    fun fromNetwork(network: Network?): String {
        return gson.toJson(network)
    }

    @TypeConverter
    fun toNetwork(value: String): Network? {
        return gson.fromJson(value, Network::class.java)
    }

    // Конвертер для Rating
    @TypeConverter
    fun fromRating(rating: Rating?): String {
        return gson.toJson(rating)
    }

    @TypeConverter
    fun toRating(value: String): Rating? {
        return gson.fromJson(value, Rating::class.java)
    }
}