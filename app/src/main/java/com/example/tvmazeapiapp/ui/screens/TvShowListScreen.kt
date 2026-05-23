package com.example.tvmazeapiapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tvmazeapiapp.ui.state.TvShowListState
import com.example.tvmazeapiapp.ui.widgets.TvShowItem
import com.example.tvmazeapiapp.viewmodel.TvShowListEvent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import com.example.tvmazeapiapp.ui.state.list.TvShowListEmpty
import com.example.tvmazeapiapp.ui.state.list.TvShowListError
import com.example.tvmazeapiapp.ui.state.list.TvShowListLoading
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.layout.Row
import com.example.tvmazeapiapp.data.model.TvShow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowListScreen(
    state: TvShowListState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onEvent: (TvShowListEvent) -> Unit,
    onShowClick: (Int) -> Unit,
    onToggleFavorite: (TvShow) -> Unit,
    onFavoriteClick: () -> Unit
) {
    when (state) {
        is TvShowListState.Loading -> {
            TvShowListLoading()
        }

        is TvShowListState.Empty -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "TV Maze Api") },
                        actions = {
                            Button(
                                onClick = onFavoriteClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFA61B06),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Favorites")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            onSearchQueryChange(query)
                            onEvent(TvShowListEvent.Search(query))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Search by title") },
                        singleLine = true,
                        placeholder = { Text("Enter show name...") },
                        shape = RoundedCornerShape(16.dp)
                    )

                    TvShowListEmpty()
                }
            }
        }

        is TvShowListState.Success -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "TV Maze Api") },
                        actions = {
                            Button(
                                onClick = onFavoriteClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFA61B06),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Favorites")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            onSearchQueryChange(query)
                            onEvent(TvShowListEvent.Search(query))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Search by title") },
                        singleLine = true,
                        placeholder = { Text("Enter show name...") },
                        shape = RoundedCornerShape(16.dp)
                    )

                    if (state.shows.isEmpty()) {
                        TvShowListEmpty()
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.shows,
                                key = { it.id }
                            ) { show ->
                                TvShowItem(
                                    show = show,
                                    onClick = { onShowClick(show.id) },
                                    onToggleFavorite = onToggleFavorite
                                )
                            }

                            item {
                                Button(
                                    onClick = { onEvent(TvShowListEvent.LoadMore) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2196F3),
                                        contentColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF21303D)),
                                ) {
                                    Text("Load More")
                                }
                            }
                        }
                    }
                }
            }
        }

        is TvShowListState.Error -> {
            TvShowListError(
                state = state,
                onEvent = onEvent
            )
        }
    }
}