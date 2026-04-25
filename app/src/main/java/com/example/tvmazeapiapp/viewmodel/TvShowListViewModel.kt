package com.example.tvmazeapiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.remote.repository.TvShowRepository
import com.example.tvmazeapiapp.ui.state.TvShowListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TvShowListEvent {
    object LoadShows : TvShowListEvent()
    data class Search(val query: String) : TvShowListEvent()
    object Refresh : TvShowListEvent()
    object Retry : TvShowListEvent()
    object LoadMore : TvShowListEvent()
}

@HiltViewModel
class TvShowListViewModel @Inject constructor(
    private val repository: TvShowRepository
) : ViewModel() {
    private val _state = MutableStateFlow<TvShowListState>(TvShowListState.Loading)
    val state: StateFlow<TvShowListState> = _state.asStateFlow()

    private var currentPage = 0
    private var allShows = mutableListOf<TvShow>()
    private var currentQuery: String? = null
    private var isLoadingMore = false
    private var isSearching = false
    private var searchJob: Job? = null


    fun onEvent(event: TvShowListEvent) {
        when (event) {
            is TvShowListEvent.LoadShows -> loadShows()
            is TvShowListEvent.Search -> searchShows(event.query)
            is TvShowListEvent.Refresh -> refresh()
            is TvShowListEvent.Retry -> retry()
            is TvShowListEvent.LoadMore -> loadMore()
        }
    }

    private fun loadShows() {
        viewModelScope.launch {
            _state.value = TvShowListState.Loading
            currentQuery = null
            currentPage = 0
            allShows.clear()
            isLoadingMore = false

            val result = repository.getShows(page = currentPage)
            if (result.isSuccess) {
                val shows = result.getOrNull() ?: emptyList()

                val favorites = repository.getFavorites()
                val favoriteIds = favorites.map { it.id }

                val showsWithFavorites = shows.map { show ->
                    show.copy(isFavorite = favoriteIds.contains(show.id))
                }

                val first25 = showsWithFavorites.take(25)
                allShows.addAll(first25)
                currentPage++
                _state.value = TvShowListState.Success(allShows.toList())
            } else {
                _state.value = TvShowListState.Error(result.exceptionOrNull()?.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            val result = if (isSearching && currentQuery != null) {
                repository.searchShows(currentQuery!!)
            } else {
                repository.getShows(page = currentPage)
            }

            if (result.isSuccess) {
                val newShows = result.getOrNull().orEmpty()

                val favorites = repository.getFavorites()
                val favoriteIds = favorites.map { it.id }

                val showsWithFavorites = newShows.map { show ->
                    show.copy(isFavorite = favoriteIds.contains(show.id))
                }

                if (!isSearching) {
                    val next25 = showsWithFavorites.take(25)
                    allShows.addAll(next25)
                    currentPage++
                } else {
                    allShows.addAll(newShows)
                }

                _state.value = TvShowListState.Success(allShows.toList())
            } else {
                _state.value = TvShowListState.Error("Error loading more")
            }
        }
    }

    fun searchShows(query: String) {
        viewModelScope.launch {
            isSearching = true
            currentQuery = query
            currentPage = 0
            allShows.clear()

            if (query.isBlank()) {
                refresh()
            }

            val result = repository.searchShows(query)

            if (result.isSuccess) {
                val shows = result.getOrNull().orEmpty()

                val favorites = repository.getFavorites()
                val favoriteIds = favorites.map { it.id }

                val showsWithFavorites = shows.map { show ->
                    show.copy(isFavorite = favoriteIds.contains(show.id))
                }

                _state.value = if (showsWithFavorites.isEmpty()) {
                    TvShowListState.Empty
                } else {
                    TvShowListState.Success(shows)
                }
            } else {
                _state.value = TvShowListState.Error("Search failed")
            }
        }
    }

    private fun refresh() {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            if (currentQuery.isNullOrBlank()) {
                loadShows()
            } else {
                searchShows(currentQuery!!)
            }
        }
    }

    private fun retry() {
        refresh()
    }

    fun toggleFavorite(show: TvShow) {
        viewModelScope.launch {
            val newState = !show.isFavorite
            repository.setFavorite(show, newState)

            val index = allShows.indexOfFirst { it.id == show.id }
            if (index != -1) {
                allShows[index] = allShows[index].copy(isFavorite = newState)
            }

            val current = _state.value
            if (current is TvShowListState.Success) {
                val updated = current.shows.map {
                    if (it.id == show.id) it.copy(isFavorite = newState) else it
                }
                _state.value = TvShowListState.Success(updated)
            }
        }
    }
}