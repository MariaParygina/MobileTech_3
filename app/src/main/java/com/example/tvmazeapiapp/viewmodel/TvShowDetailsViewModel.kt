package com.example.tvmazeapiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.repository.TvShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TvShowDetailsState {
    object Loading : TvShowDetailsState()
    data class Success(val show: TvShow) : TvShowDetailsState()
    data class Error(val message: String) : TvShowDetailsState()
}

class TvShowDetailsViewModel(
    private val repository: TvShowRepository
) : ViewModel() {
    private val _state = MutableStateFlow<TvShowDetailsState>(TvShowDetailsState.Loading)
    val state: StateFlow<TvShowDetailsState> = _state

    fun loadShow(id: Int) {
        viewModelScope.launch {
            _state.value = TvShowDetailsState.Loading

            try {
                val show = repository.getShowById(id)
                if (show != null) {
                    _state.value = TvShowDetailsState.Success(show)
                } else {
                    _state.value = TvShowDetailsState.Error("Show not found")
                }
            } catch (e: Exception) {
                _state.value = TvShowDetailsState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun retry(id: Int) {
        loadShow(id)
    }
}