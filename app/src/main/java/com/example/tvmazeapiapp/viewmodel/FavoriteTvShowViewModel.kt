package com.example.tvmazeapiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.remote.repository.TvShowRepository
import com.example.tvmazeapiapp.ui.state.TvShowListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: TvShowRepository
) : ViewModel() {
    private val _state = MutableStateFlow<TvShowListState>(TvShowListState.Loading)
    val state: StateFlow<TvShowListState> = _state.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _state.value = TvShowListState.Loading
            try {
                val favorites = repository.getFavorites()
                if (favorites.isEmpty()) {
                    _state.value = TvShowListState.Empty
                } else {
                    _state.value = TvShowListState.Success(favorites)
                }
            } catch (e: Exception) {
                _state.value = TvShowListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun removeFromFavorites(showId: Int) {
        val currentState = _state.value
        if (currentState is TvShowListState.Success) {
            val updatedShows = currentState.shows.filter { it.id != showId }
            _state.value = if (updatedShows.isEmpty()) {
                TvShowListState.Empty
            } else {
                TvShowListState.Success(updatedShows)
            }
        }
    }

    fun refresh() {
        loadFavorites()
    }
}