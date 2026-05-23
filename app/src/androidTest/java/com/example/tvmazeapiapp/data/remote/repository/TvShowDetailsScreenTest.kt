package com.example.tvmazeapiapp.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.tvmazeapiapp.data.model.Country
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsState
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test

class TvShowDetailsScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testShow = TvShow(
        id = 1,
        name = "Test Show",
        rating = Rating(average = 8.5),
        network = Network(country = Country(name = "USA"), holder = "HBO"),
        genres = listOf("Drama"),
        status = "Running",
        premiered = "2023-01-01",
        ended = null,
        officialSite = "https://hbo.com/test",
        summary = "<p>Great show</p>",
        image = null,
        isFavorite = false
    )

    @Test
    fun detailsScreenShowsLoadingIndicatorWhileLoading() {
        val mockViewModel = mockk<TvShowDetailsViewModel>(relaxed = true)
        val stateFlow = MutableStateFlow<TvShowDetailsState>(TvShowDetailsState.Loading)

        coEvery { mockViewModel.state } returns stateFlow
        coEvery { mockViewModel.loadShow(any()) } returns Unit

        composeTestRule.setContent {
            TvShowDetailsScreen(
                id = 1,
                viewModel = mockViewModel,
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Loading show details...").assertIsDisplayed()
    }
}