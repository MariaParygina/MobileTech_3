package com.example.tvmazeapiapp.data.remote.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tvmazeapiapp.data.TvShowDatabase
import com.example.tvmazeapiapp.data.api.TvMazeApi
import com.example.tvmazeapiapp.data.model.Country
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.TvShow
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TvShowRepositoryTest {
    private lateinit var database: TvShowDatabase
    private lateinit var repository: TvShowRepository

    private val testShow = TvShow(
        id = 1, name = "Test Show",
        rating = Rating(average = 8.5),
        network = Network(country = Country(name = "USA"), holder = "HBO"),
        genres = listOf("Drama", "Thriller"),
        status = "Running", premiered = "2023-01-01",
        ended = null, officialSite = "https://hbo.com/test",
        summary = "<p>Great show</p>", image = null,
        isFavorite = false
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TvShowDatabase::class.java
        ).allowMainThreadQueries().build()

        val fakeApi = object : TvMazeApi {
            override suspend fun getShows(page: Int): List<TvShow> = emptyList()
            override suspend fun searchShows(query: String): List<TvMazeApi.SearchResult> = emptyList()
            override suspend fun getShowById(id: Int): TvShow = testShow
        }

        repository = TvShowRepository(fakeApi, database.tvshowDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    // 1 - repository + room: добавление и получение избранного
    @Test
    fun addFavoriteAndRetrieveFromRoom() = runTest {
        repository.setFavorite(testShow, true)

        val favorites = repository.getFavorites()
        assertEquals(1, favorites.size)
        assertEquals(testShow.id, favorites[0].id)
        assertEquals(testShow.name, favorites[0].name)
    }

    // 2 - удаление из избранного
    @Test
    fun removeFavoriteFromRoom() = runTest {
        repository.setFavorite(testShow, true)
        repository.setFavorite(testShow, false)

        val favorites = repository.getFavorites()
        assertTrue("Favorites should be empty", favorites.isEmpty())
    }

    // 3 - отсутствие дублей
    @Test
    fun noDuplicateFavorite() = runTest {
        repository.setFavorite(testShow, true)
        repository.setFavorite(testShow, true)

        val favorites = repository.getFavorites()
        assertEquals("Should not create duplicate", 1, favorites.size)
    }
}