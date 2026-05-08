package com.example.tvmazeapiapp.viewmodel

import com.example.tvmazeapiapp.data.model.Country
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.remote.repository.TvShowRepository
import com.example.tvmazeapiapp.ui.state.TvShowListState
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TvShowListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: TvShowRepository = mockk(relaxed = true)
    private lateinit var viewModel: TvShowListViewModel

    private val show1 = TvShow(
        id = 1, name = "Show 1",
        rating = Rating(average = 8.0),
        network = Network(country = Country(name = "USA"), holder = "NBC"),
        genres = listOf("Drama"), status = "Running",
        premiered = "2020-01-01", ended = null,
        officialSite = "https://test.com", summary = "Test",
        image = null, isFavorite = false
    )

    private val show2 = TvShow(
        id = 2, name = "Show 2",
        rating = Rating(average = 7.5),
        network = Network(country = Country(name = "UK"), holder = "BBC"),
        genres = listOf("Comedy"), status = "Ended",
        premiered = "2019-01-01", ended = "2021-01-01",
        officialSite = null, summary = "Test 2",
        image = null, isFavorite = false
    )

    @Before
    fun setUp() {
        viewModel = TvShowListViewModel(repository)
        coEvery { repository.getFavorites() } returns emptyList()
    }

    // 1 - начальное состояние
    @Test
    fun `initial state is loading`() = runTest {
        val state = viewModel.state.value
        assertTrue("Expected Loading state but got $state", state is TvShowListState.Loading)
    }

    // 2 - успешная загрузка
    @Test
    fun `loadShows success state with shows`() = runTest {
        val shows = listOf(show1, show2)
        coEvery { repository.getShows(page = 0) } returns Result.success(shows)

        viewModel.onEvent(TvShowListEvent.LoadShows)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Success state", state is TvShowListState.Success)
        assertEquals(2, (state as TvShowListState.Success).shows.size)
        assertEquals("Show 1", state.shows[0].name)
    }

    // 3 - ошибка загрузки
    @Test
    fun `loadShows error state`() = runTest {
        val errorMessage = "Network error"
        coEvery { repository.getShows(page = 0) } returns Result.failure(Exception(errorMessage))

        viewModel.onEvent(TvShowListEvent.LoadShows)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Error state", state is TvShowListState.Error)
        assertEquals(errorMessage, (state as TvShowListState.Error).message)
    }

    // 4 - поиск с результатами
    @Test
    fun `search with results shows success`() = runTest {
        val searchResults = listOf(show1)
        coEvery { repository.searchShows("show") } returns Result.success(searchResults)

        viewModel.onEvent(TvShowListEvent.Search("show"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Success state", state is TvShowListState.Success)
        assertEquals(1, (state as TvShowListState.Success).shows.size)
    }

    // 5 - refresh сбрасывает состояние и перезагружает
    @Test
    fun `refresh clears state and reloads shows`() = runTest {
        val shows = listOf(show1)
        coEvery { repository.getShows(page = 0) } returns Result.success(shows)

        viewModel.onEvent(TvShowListEvent.Refresh)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Success after refresh", state is TvShowListState.Success)
        assertEquals(1, (state as TvShowListState.Success).shows.size)
    }

    // НЕТРИВИАЛЬНЫЕ ТЕСТЫ
    // 1 - retry после ошибки
    @Test
    fun `retry after error call to repository again and recovers`() = runTest {
        val shows = listOf(show1)
        coEvery { repository.getShows(page = 0) } returnsMany
                listOf(
                    Result.failure(Exception("First attempt failed")),
                    Result.success(shows)
                )

        viewModel.onEvent(TvShowListEvent.LoadShows)
        advanceUntilIdle()
        assertTrue(viewModel.state.value is TvShowListState.Error)

        viewModel.onEvent(TvShowListEvent.Retry)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Success after retry", state is TvShowListState.Success)
        assertEquals(1, (state as TvShowListState.Success).shows.size)

        coVerify(exactly = 2) { repository.getShows(page = 0) }
    }

    // 2 - toggleFavorite обновляет состояние
    @Test
    fun `toggleFavorite updates isFavorite flag in state`() = runTest {
        val shows = mutableListOf(show1.copy(isFavorite = false))
        coEvery { repository.getShows(page = 0) } returns Result.success(shows)
        coEvery { repository.setFavorite(any(), any()) } just Runs

        viewModel.onEvent(TvShowListEvent.LoadShows)
        advanceUntilIdle()

        viewModel.toggleFavorite(show1)
        advanceUntilIdle()

        val state = viewModel.state.value as TvShowListState.Success
        assertTrue("Show should be favorite", state.shows[0].isFavorite)

        viewModel.toggleFavorite(show1.copy(isFavorite = true))
        advanceUntilIdle()

        val updatedState = viewModel.state.value as TvShowListState.Success
        assertFalse("Show should not be favorite", updatedState.shows[0].isFavorite)
    }

    // ТЕСТЫ ДЛЯ FLOW
    // 1 - flow: тестирование потока состояний loading - success
    @Test
    fun `stateFlow emits Loading then Success sequence`() = runTest {
        val shows = listOf(show1)
        coEvery { repository.getShows(page = 0) } returns Result.success(shows)

        val states = mutableListOf<TvShowListState>()
        val job = launch {
            viewModel.state.toList(states)
        }

        viewModel.onEvent(TvShowListEvent.LoadShows)
        advanceUntilIdle()

        assertEquals(2, states.size)
        assertTrue("First should be Loading", states[0] is TvShowListState.Loading)
        assertTrue("Second should be Success", states[1] is TvShowListState.Success)

        job.cancel()
    }

    // НЕТРИВИАЛЬНЫЙ FLOW
    // 2 - flow: отмена устаревшего поиска
    @Test
    fun `rapid search cancels previous request and shows latest result`() = runTest {
        val slowShow = show1.copy(name = "Slow Result")
        val fastShow = show2.copy(name = "Fast Result")

        coEvery { repository.searchShows("sl") } coAnswers {
            delay(1000)
            Result.success(listOf(slowShow))
        }
        coEvery { repository.searchShows("slo") } returns Result.success(listOf(fastShow))

        viewModel.onEvent(TvShowListEvent.Search("sl"))
        viewModel.onEvent(TvShowListEvent.Search("slo"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Success state", state is TvShowListState.Success)

        val shows = (state as TvShowListState.Success).shows
        assertEquals(1, shows.size)
        assertEquals("Fast Result", shows[0].name)
    }
}