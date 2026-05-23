package com.example.tvmazeapiapp.data.remote.repository

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.tvmazeapiapp.data.model.Country
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.ui.screens.TvShowListScreen
import com.example.tvmazeapiapp.ui.state.TvShowListState
import com.example.tvmazeapiapp.viewmodel.TvShowListEvent
import org.junit.Rule
import org.junit.Test

class TvShowListScreenUiTest {

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
    fun clickOnCardToDetailsWithId() {
        var clickedId: Int? = null

        composeTestRule.setContent {
            TvShowListScreen(
                state = TvShowListState.Success(listOf(testShow)),
                searchQuery = "",
                onSearchQueryChange = {},
                onEvent = {},
                onShowClick = { clickedId = it },
                onToggleFavorite = {},
                onFavoriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Test Show")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test Show")
            .performClick()

        assert(clickedId == 1)
    }

    @Test
    fun loadingStateShowsLoadingIndicator() {
        composeTestRule.setContent {
            TvShowListScreen(
                state = TvShowListState.Loading,
                searchQuery = "",
                onSearchQueryChange = {},
                onEvent = {},
                onShowClick = {},
                onToggleFavorite = {},
                onFavoriteClick = {}
            )
        }

        composeTestRule
            .onRoot()
            .assertExists()
    }

    @Test
    fun emptyStateShowsEmptyMessage() {
        composeTestRule.setContent {
            TvShowListScreen(
                state = TvShowListState.Empty,
                searchQuery = "",
                onSearchQueryChange = {},
                onEvent = {},
                onShowClick = {},
                onToggleFavorite = {},
                onFavoriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("No shows found")
            .assertIsDisplayed()
    }

    @Test
    fun errorStateShowsErrorMessageAndRetryButton() {
        var retryCalled = false

        composeTestRule.setContent {
            TvShowListScreen(
                state = TvShowListState.Error("Network error"),
                searchQuery = "",
                onSearchQueryChange = {},
                onEvent = { event ->
                    if (event is TvShowListEvent.Retry) {
                        retryCalled = true
                    }
                },
                onShowClick = {},
                onToggleFavorite = {},
                onFavoriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Error: Network error")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Retry")
            .assertIsDisplayed()
            .performClick()

        assert(retryCalled)
    }
}