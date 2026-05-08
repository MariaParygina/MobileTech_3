package com.example.tvmazeapiapp.viewmodel

import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.remote.repository.TvShowRepository
import com.example.tvmazeapiapp.data.model.Network
import com.example.tvmazeapiapp.data.model.Rating
import com.example.tvmazeapiapp.data.model.Country
import com.example.tvmazeapiapp.data.model.Image
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

class TvShowListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: TvShowRepository = mockk()

    private val favoriteTvShow = TvShow(
        id = 1,
        name = "Under the Dome",
        rating = Rating(average = 6.6),
        network = Network(
            country = Country(name = "USA"),
            holder = "CBS"
        ),
        genres = listOf("Drama", "Sci-Fi", "Mystery"),
        status = "Ended",
        premiered = "2013-06-24",
        ended = "2015-09-10",
        officialSite = "https://www.cbs.com/shows/under-the-dome/",
        summary = "<p>Under the Dome is based on the novel by Stephen King.</p>",
        image = Image(medium = "https://example.com/image.jpg"),
        isFavorite = true
    )

    @Before
    fun setUp() {
        coEvery { repository.getFavorites() } returns emptyList()
    }

}
