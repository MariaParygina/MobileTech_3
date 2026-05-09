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
    fun `loadShow error sets Error state`() = runTest {
        val errorMessage = "Show not found"
        coEvery { repository.getShowById(999) } throws Exception(errorMessage)

        viewModel.loadShow(999)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Error state", state is TvShowDetailsState.Error)
        assertEquals(errorMessage, (state as TvShowDetailsState.Error).message)
    }

    // 3 - загрузка несуществующего шоу
    @Test
    fun `loadShow returns Error when show is null`() = runTest {
        coEvery { repository.getShowById(123) } throws NullPointerException("Show not found")

        viewModel.loadShow(123)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Error state", state is TvShowDetailsState.Error)
        assertEquals("Show not found", (state as TvShowDetailsState.Error).message)
    }

    // UI
    // 4 - клик по элементу -> loading -> переход на детали
    @Test
    fun `click on card loads show details correctly`() = runTest {
        // arrange
        val showId = 1
        coEvery { repository.getShowById(showId) } returns testShow

        // act - симулируем клик по элементу
        viewModel.loadShow(showId)
        advanceUntilIdle()

        // assert - проверяем только конечный результат
        val state = viewModel.state.value
        assertTrue("State should be Success", state is TvShowDetailsState.Success)

        val show = (state as TvShowDetailsState.Success).show
        assertEquals("Show ID should match", showId, show.id)
        assertEquals("Show name should match", "Test Show", show.name)
        assertEquals("Network should be HBO", "HBO", show.network?.holder)

        coVerify(exactly = 1) { repository.getShowById(showId) }
    }
}