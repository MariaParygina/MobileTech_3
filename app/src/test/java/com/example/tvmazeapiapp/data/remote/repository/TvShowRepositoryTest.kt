package com.example.tvmazeapiapp.data.remote.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.tvmazeapiapp.data.TvShowDatabase
import com.example.tvmazeapiapp.data.local.FavoriteTvShowEntity
import com.example.tvmazeapiapp.data.local.toFavoriteEntity
import com.example.tvmazeapiapp.data.model.Country
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.TvShow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TvShowRepositoryTest {
    private lateinit var database: TvShowDatabase
    private lateinit var repository: TvShowRepository

    private val testShow = TvShow(
        id = 1, name = "Test Show",
        rating = Rating(average = 8.0),
        network = Network(country = Country(name = "USA"), holder = "NBC"),
        genres = listOf("Drama"), status = "Running",
        premiered = "2023-01-01", ended = null,
        officialSite = null, summary = "Test summary",
        image = null, isFavorite = false
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TvShowDatabase::class.java
        ).build()
        repository = TvShowRepository(database.tvshowDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    // 1 - repository + room: добавление и получение избранного
    @Test
    fun `add favorite and retrieve from Room`() = runTest {
        repository.setFavorite(testShow, true)

        val favorites = repository.getFavorites()
        assertEquals(1, favorites.size)
        assertEquals(testShow.id, favorites[0].id)
        assertEquals(testShow.name, favorites[0].name)
    }

    // 2 - удаление из избранного
    @Test
    fun `remove favorite works correctly`() = runTest {
        //arrange
        repository.setFavorite(testShow, true)

        // act
        repository.setFavorite(testShow, false)
        // assert
        val favorites = repository.getFavorites()
        assertTrue("Favorites should be empty", favorites.isEmpty())
    }

    // НЕТРИВИАЛЬНЫЙ ТЕСТ
    // нет дублей при повторном добавлении карточки
    @Test
    fun `no duplicate favorite creating`() = runTest {
        repository.setFavorite(testShow, true)
        repository.setFavorite(testShow, true)

        val favorites = repository.getFavorites()
        assertEquals("Should not create duplicate", 1, favorites.size)
    }
}