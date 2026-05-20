package com.example.tvmazeapiapp.viewmodel

import androidx.annotation.Nullable
import com.example.tvmazeapiapp.data.model.Country
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.remote.repository.TvShowRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TvShowDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: TvShowRepository = mockk()
    private lateinit var viewModel: TvShowDetailsViewModel

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
        viewModel = TvShowDetailsViewModel(repository)
    }

    // 1 - успешная загрузка деталей
    @Test
    fun `loadShow success sets Success state`() = runTest {
        coEvery { repository.getShowById(1) } returns testShow

        viewModel.loadShow(1)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Success state", state is TvShowDetailsState.Success)

        val show = (state as TvShowDetailsState.Success).show
        assertEquals(1, show.id)
        assertEquals("Test Show", show.name)
        assertEquals("HBO", show.network?.holder)
    }

    // 2 - ошибка загрузки
    @Test
    fun `loadShow emits Error when repository throws exception`() = runTest {

        coEvery {
            repository.getShowById(123)
        } throws RuntimeException("Network error")

        viewModel.loadShow(123)

        advanceUntilIdle()

        val state = viewModel.state.value

        assertTrue(state is TvShowDetailsState.Error)

        state as TvShowDetailsState.Error

        assertEquals("Network error", state.message)
    }

    // UI
    // 3 - клик по элементу -> loading -> переход на детали
    @Test
    fun `loadShow emits Success with tv show`() = runTest {

        val showId = 1

        coEvery {
            repository.getShowById(showId)
        } returns testShow

        viewModel.loadShow(showId)

        advanceUntilIdle()

        val state = viewModel.state.value

        assertTrue(state is TvShowDetailsState.Success)

        state as TvShowDetailsState.Success

        assertEquals(testShow.id, state.show.id)
        assertEquals(testShow.name, state.show.name)
    }
}