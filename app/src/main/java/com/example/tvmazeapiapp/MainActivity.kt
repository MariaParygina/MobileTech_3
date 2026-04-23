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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import com.example.tvmazeapiapp.data.api.TvMazeApi
import com.example.tvmazeapiapp.data.repository.TvShowRepository
import com.example.tvmazeapiapp.di.NetworkModule
import com.example.tvmazeapiapp.ui.screens.TvShowListScreen
import com.example.tvmazeapiapp.ui.theme.TVmazeApiAppTheme
import com.example.tvmazeapiapp.viewmodel.TvShowListEvent
import com.example.tvmazeapiapp.viewmodel.TvShowListViewModel
import com.example.tvmazeapiapp.viewmodel.TvShowListViewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tvmazeapiapp.di.ShowRoutes
import com.example.tvmazeapiapp.ui.screens.TvShowDetailsScreen
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsViewModel
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsViewModelFactory

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

    val api = NetworkModule.api.create(TvMazeApi::class.java)
    val repository = TvShowRepository(api)

    val listViewModel: TvShowListViewModel = viewModel(
        factory = TvShowListViewModelFactory(repository)
    )

    val state by listViewModel.state.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        listViewModel.onEvent(TvShowListEvent.LoadShows)
    }

    NavHost(
        navController = navController,
        startDestination = ShowRoutes.LIST_ROUTE
    ) {
        composable(ShowRoutes.LIST_ROUTE) {
            TvShowListScreen(
                state = state,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onEvent = listViewModel::onEvent,
                onShowClick = { showId ->
                    navController.navigate(ShowRoutes.details(showId))
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
                val detailsViewModel: TvShowDetailsViewModel = viewModel(
                    factory = TvShowDetailsViewModelFactory(repository)
                )

                TvShowDetailsScreen(
                    id = showId,
                    viewModel = detailsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}