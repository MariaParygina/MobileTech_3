package com.example.tvmazeapiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvmazeapiapp.data.repository.TvShowRepository

class TvShowDetailsViewModelFactory(
    private val repository: TvShowRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TvShowDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TvShowDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}