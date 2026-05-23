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
import kotlinx.coroutines.test.advanceTimeBy
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
        clearMocks(repository)
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
    fun `searchShows returns results`() = runTest {
        val results = listOf(show1)

        coEvery {
            repository.searchShows("show")
        } returns Result.success(results)

        coEvery { repository.getFavorites() } returns emptyList()

        viewModel.onEvent(TvShowListEvent.Search("show"))

        advanceUntilIdle()

        val state = viewModel.state.value

        assertTrue(state is TvShowListState.Success)

        state as TvShowListState.Success

        assertEquals(1, state.shows.size)
        assertEquals("Show 1", state.shows.first().name)
    }

    // 5
    @Test
    fun `searchShows emits Empty when nothing found`() = runTest {
        coEvery {
            repository.searchShows("unknown")
        } returns Result.success(emptyList())

        coEvery { repository.getFavorites() } returns emptyList()

        viewModel.onEvent(TvShowListEvent.Search("unknown"))

        advanceUntilIdle()

        assertEquals(TvShowListState.Empty, viewModel.state.value)
    }

    // 6 - refresh сбрасывает состояние и перезагружает
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
        val shows = listOf(show1.copy(isFavorite = false))
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

        val emissions = mutableListOf<TvShowListState>()
        val collectJob = launch {
            viewModel.state.toList(emissions)
        }

        advanceUntilIdle()

        val initialEmissionsCount = emissions.size
        println("Initial emissions: $emissions")

        // act
        viewModel.onEvent(TvShowListEvent.LoadShows)
        advanceUntilIdle()

        // assert
        val loadingIndex = emissions.indexOfLast { it is TvShowListState.Loading }
        val successIndex = emissions.indexOfLast { it is TvShowListState.Success }

        assertTrue("Should have Loading state", loadingIndex != -1)
        assertTrue("Should have Success state", successIndex != -1)
        assertTrue("Loading should come before Success", loadingIndex < successIndex)

        assertTrue("Last state should be Success", emissions.last() is TvShowListState.Success)

        collectJob.cancel()
    }

    // 2 - flow: отмена устаревшего поиска
    @Test
    fun `search flow emits latest successful result`() = runTest {

        val slowShow = show1.copy(name = "Slow Result")
        val fastShow = show2.copy(name = "Fast Result")

        coEvery {
            repository.searchShows("sl")
        } coAnswers {
            delay(1000)
            Result.success(listOf(slowShow))
        }

        coEvery {
            repository.searchShows("slo")
        } returns Result.success(listOf(fastShow))

        coEvery { repository.getFavorites() } returns emptyList()

        val emissions = mutableListOf<TvShowListState>()

        val collectJob = launch {
            viewModel.state.toList(emissions)
        }

        viewModel.onEvent(TvShowListEvent.Search("sl"))

        advanceTimeBy(100)

        viewModel.onEvent(TvShowListEvent.Search("slo"))

        advanceUntilIdle()

        val finalState = viewModel.state.value

        assertTrue(finalState is TvShowListState.Success)

        finalState as TvShowListState.Success

        assertEquals("Slow Result", finalState.shows.first().name)

        val successStates = emissions.filterIsInstance<TvShowListState.Success>()

        assertTrue(successStates.isNotEmpty())

        assertTrue(
            successStates.any {
                it.shows.firstOrNull()?.name == "Fast Result"
            }
        )

        assertTrue(
            successStates.any {
                it.shows.firstOrNull()?.name == "Slow Result"
            }
        )

        collectJob.cancel()
    }
}