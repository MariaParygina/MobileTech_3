package com.example.tvmazeapiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import com.example.tvmazeapiapp.ui.screens.FavoriteTvShowListScreen
import com.example.tvmazeapiapp.ui.screens.TvShowListScreen
import com.example.tvmazeapiapp.ui.theme.TVmazeApiAppTheme
import com.example.tvmazeapiapp.viewmodel.TvShowListEvent
import com.example.tvmazeapiapp.viewmodel.TvShowListViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tvmazeapiapp.di.ShowRoutes
import com.example.tvmazeapiapp.ui.screens.TvShowDetailsScreen
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TVmazeApiAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TvShowApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TvShowApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ShowRoutes.LIST_ROUTE
    ) {
        composable(ShowRoutes.LIST_ROUTE) {
            val listViewModel: TvShowListViewModel = hiltViewModel()
            val state by listViewModel.state.collectAsState()
            var searchQuery by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                listViewModel.onEvent(TvShowListEvent.LoadShows)
            }

            TvShowListScreen(
                state = state,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onEvent = listViewModel::onEvent,
                onShowClick = { showId ->
                    navController.navigate(ShowRoutes.details(showId))
                },
                onFavoriteClick = {
                    navController.navigate("favorites")
                },
                onToggleFavorite = { show ->
                    listViewModel.toggleFavorite(show)
                }
            )
        }

        composable("favorites") {
            val listViewModel: TvShowListViewModel = hiltViewModel()
            FavoriteTvShowListScreen(
                onBackClick = { navController.popBackStack() },
                onShowClick = { showId ->
                    navController.navigate(ShowRoutes.details(showId))
                },
                onToggleFavorite = { show ->
                    listViewModel.toggleFavorite(show)
                }
            )
        }

        composable(
            route = ShowRoutes.DETAILS_ROUTE_PATTERN,
            arguments = listOf(
                navArgument(ShowRoutes.SHOW_ID_ARG) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val showId = backStackEntry.arguments
                ?.getInt(ShowRoutes.SHOW_ID_ARG)

            if (showId != null) {
                val detailsViewModel: TvShowDetailsViewModel = hiltViewModel()

                TvShowDetailsScreen(
                    id = showId,
                    viewModel = detailsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}