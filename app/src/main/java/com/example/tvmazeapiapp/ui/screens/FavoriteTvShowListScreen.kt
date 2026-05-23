package com.example.tvmazeapiapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.ui.state.TvShowListState
import com.example.tvmazeapiapp.ui.state.list.TvShowListEmpty
import com.example.tvmazeapiapp.ui.state.list.TvShowListLoading
import com.example.tvmazeapiapp.ui.widgets.TvShowItem
import com.example.tvmazeapiapp.viewmodel.FavoriteViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteTvShowListScreen(
    onBackClick: () -> Unit,
    onShowClick: (Int) -> Unit,
    onToggleFavorite: (TvShow) -> Unit
) {
    val viewModel: FavoriteViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            is TvShowListState.Loading -> {
                TvShowListLoading()
            }

            is TvShowListState.Empty -> {
                TvShowListEmpty()
            }

            is TvShowListState.Success -> {
                val shows = (state as TvShowListState.Success).shows

                if (shows.isEmpty()) {
                    TvShowListEmpty()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(shows) { show ->
                            TvShowItem(
                                show = show,
                                onClick = { onShowClick(show.id) },
                                onToggleFavorite = {
                                    onToggleFavorite(show)
                                    viewModel.removeFromFavorites(show.id)
                                }
                            )
                        }
                    }
                }
            }

            is TvShowListState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error")
                }
            }
        }
    }
}