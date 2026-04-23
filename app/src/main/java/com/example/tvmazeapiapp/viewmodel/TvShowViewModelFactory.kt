package com.example.tvmazeapiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvmazeapiapp.data.repository.TvShowRepository

class TvShowListViewModelFactory(
    private val repository: TvShowRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TvShowListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TvShowListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}