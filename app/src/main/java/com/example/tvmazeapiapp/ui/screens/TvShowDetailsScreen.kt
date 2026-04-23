package com.example.tvmazeapiapp.ui.screens

import android.text.Html
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.tvmazeapiapp.ui.state.details.TvShowDetailsError
import com.example.tvmazeapiapp.ui.state.details.TvShowListLoadingDetails
import com.example.tvmazeapiapp.ui.theme.cardBorder
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsState
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsViewModel

@Composable
fun LabeledText(label: String, value: String) {
    Row {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = ": ",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = value,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailsScreen(
    id: Int,
    viewModel: TvShowDetailsViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadShow(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Details") })
        }
    ) { innerPadding ->
        when (state) {
            is TvShowDetailsState.Loading -> {
                TvShowListLoadingDetails()
            }

            is TvShowDetailsState.Success -> {
                val show = (state as TvShowDetailsState.Success).show

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Button(
                            onClick = onBackClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE1E0E7),
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(1.dp, cardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back")
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (show.image?.medium != null) {
                                AsyncImage(
                                    model = show.image.medium,
                                    contentDescription = show.name,
                                    modifier = Modifier
                                        .size(width = 120.dp, height = 180.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        if (!show.network?.country?.name.isNullOrEmpty()) {
                            LabeledText("Country", show.network?.country?.name ?: "")
                        } else {
                            LabeledText("Country", "Not specified")
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        if (!show.network?.holder.isNullOrEmpty()) {
                            LabeledText("TV company", show.network?.holder ?: "")
                        } else {
                            LabeledText("TV company", "Not specified")
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        if (!show.officialSite.isNullOrEmpty()) {
                            LabeledText("Website", show.officialSite ?: "")
                        } else {
                            LabeledText("Website", "Not specified")
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        LabeledText("Status", show.status ?: "Not specified")

                        Spacer(modifier = Modifier.height(5.dp))

                        LabeledText("Premiered", show.premiered ?: "Not specified")

                        Spacer(modifier = Modifier.height(5.dp))

                        if (!show.ended.isNullOrEmpty()) {
                            LabeledText("Ended", show.ended)
                        } else {
                            LabeledText("Ended", "Still running")
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Description:",
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        val cleanSummary = remember(show.summary) {
                            if (!show.summary.isNullOrEmpty()) {
                                Html.fromHtml(show.summary, Html.FROM_HTML_MODE_COMPACT).toString()
                            } else {
                                "No summary available"
                            }
                        }

                        Text(text = cleanSummary)
                    }
                }
            }

            is TvShowDetailsState.Error -> {
                TvShowDetailsError(
                    id = id,
                    state = state as TvShowDetailsState.Error,
                    onRetry = { viewModel.retry(id) },
                    onBackClick = onBackClick
                )
            }
        }
    }
}