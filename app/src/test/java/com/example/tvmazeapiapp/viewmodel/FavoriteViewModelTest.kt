package com.example.tvmazeapiapp.viewmodel

import com.example.tvmazeapiapp.data.model.Country
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.remote.repository.TvShowRepository
import com.example.tvmazeapiapp.ui.state.TvShowListState
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
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

    @Before
    fun setUp() {
        viewModel = FavoriteViewModel(repository)
    }

    // Unit-тест
    // 1 - когда нет любимых шоу, экран сразу показывает пустой список
    @Test
    fun `empty when no favorites exist`() = runTest {
        // arrange
        coEvery { repository.getFavorites() } returns emptyList()

        val states = mutableListOf<TvShowListState>()

        val job = launch {
            viewModel.state.collect {
                states.add(it)
            }
        }

        advanceUntilIdle()

        // act
        viewModel.loadFavorites()
        advanceUntilIdle()

        // assert
        assertTrue(states.isNotEmpty())

        assertTrue(states.first() is TvShowListState.Loading)
        assertTrue(states.last() is TvShowListState.Empty)

        coVerify(exactly = 1) {
            repository.getFavorites()
        }

        job.cancel()
    }

    // UI тест
    // 1 - удаление избранного и обновление экрана
    @Test
    fun `removeFromFavorites updates state and shows Empty when all removed`() = runTest {
        // arrange
        coEvery { repository.getFavorites() } returns listOf(favoriteShow1)
        coEvery { repository.setFavorite(any(), any()) } returns Unit

        val testViewModel = FavoriteViewModel(repository)

        testViewModel.loadFavorites()
        advanceUntilIdle()

        // act
        testViewModel.removeFromFavorites(favoriteShow1.id)
        advanceUntilIdle()

        // assert
        coVerify(exactly = 1) {
            repository.setFavorite(favoriteShow1, false)
        }

        val state = testViewModel.state.value

        assertTrue(state is TvShowListState.Empty)
    }
}