package com.example.tvmazeapiapp.ui.state

import com.example.tvmazeapiapp.data.model.TvShow

data class TvMazeUiState(
    val searchQuery: String = "",
    val showList: List<TvShow> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasSearched: Boolean = false,
)

sealed class TvShowListState {
    object Loading : TvShowListState()
    object Empty : TvShowListState()
    data class Success(val shows: List<TvShow>) : TvShowListState()
    data class Error(val message: String) : TvShowListState()
}
