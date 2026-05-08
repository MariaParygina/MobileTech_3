package com.example.tvmazeapiapp.viewmodel

import com.example.tvmazeapiapp.data.model.Country
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.remote.repository.TvShowRepository
import com.example.tvmazeapiapp.ui.state.TvShowListState
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: TvShowRepository = mockk()
    private lateinit var viewModel: FavoriteViewModel

    private val favoriteShow1 = TvShow(
        id = 1, name = "Under the Dome",
        rating = Rating(average = 6.6),
        network = Network(country = Country(name = "USA"), holder = "CBS"),
        genres = listOf("Drama", "Sci-Fi", "Mystery"),
        status = "Ended", premiered = "2013-06-24",
        ended = "2015-09-10", officialSite = "https://www.cbs.com/shows/under-the-dome/",
        summary = "<p>Under the Dome is based on the novel by Stephen King.</p>",
        image = null, isFavorite = true
    )

    private val favoriteShow2 = TvShow(
        id = 2, name = "Breaking Bad",
        rating = Rating(average = 9.5),
        network = Network(country = Country(name = "USA"), holder = "AMC"),
        genres = listOf("Drama", "Crime", "Thriller"),
        status = "Ended", premiered = "2008-01-20",
        ended = "2013-09-29", officialSite = "https://www.amc.com/shows/breaking-bad",
        summary = "<p>Breaking Bad follows Walter White.</p>",
        image = null, isFavorite = true
    )

    @Before
    fun setUp() {
        viewModel = FavoriteViewModel(repository)
    }

    // UI тесты
    // 1 - когда нет любимых шоу, экран сначала загружается, потом показывает пустой список
    @Test
    fun `loading changes to empty when no favorites exist`() = runTest {
        // arrange
        coEvery { repository.getFavorites() } returns emptyList()

        val states = mutableListOf<TvShowListState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        // act
        viewModel.refresh()
        advanceUntilIdle()

        // assert
        assertEquals("Should have exactly 2 emissions", 2, states.size)
        assertTrue("First emission should be Loading", states[0] is TvShowListState.Loading)
        assertTrue("Second emission should be Empty", states[1] is TvShowListState.Empty)

        job.cancel()
    }

    // 2 - удаление избранного и обновление экрана
    @Test
    fun `removeFromFavorites updates state and shows Empty when all removed`() = runTest {
        // arrange
        val favorites = listOf(favoriteShow1, favoriteShow2)
        coEvery { repository.getFavorites() } returns favorites

        viewModel.refresh()
        advanceUntilIdle()

        var state = viewModel.state.value
        assertTrue("Initial state should be Success", state is TvShowListState.Success)
        assertEquals("Should have 2 shows", 2, (state as TvShowListState.Success).shows.size)

        // act 1
        viewModel.removeFromFavorites(favoriteShow1.id)

        // assert 1
        state = viewModel.state.value
        assertTrue("State should still be Success", state is TvShowListState.Success)
        assertEquals("Should have 1 show left", 1, (state as TvShowListState.Success).shows.size)
        assertEquals(
            "Remaining show should be Breaking Bad",
            "Breaking Bad",
            (state as TvShowListState.Success).shows[0].name
        )

        // act 2
        viewModel.removeFromFavorites(favoriteShow2.id)

        // assert 2
        state = viewModel.state.value
        assertTrue("State should be Empty when all removed", state is TvShowListState.Empty)
    }
}