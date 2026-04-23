package com.example.tvmazeapiapp.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.ui.theme.cardBorder

@Composable
fun TvShowItem(
    show: TvShow,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = show.name,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            if (!show.network?.country?.name.isNullOrEmpty()) {
                Text(
                    text = "Country: ${show.network?.country?.name}",
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            } else {
                Text("Country: Not specified")
            }

            Spacer(modifier = Modifier.padding(1.dp))

            if (!show.genres.isNullOrEmpty()) {
                Text(text = "Genres: ${show.genres.joinToString()}")
            }

            Spacer(modifier = Modifier.padding(1.dp))

            if (show.rating?.average != null) {
                Text( text = "Rating: ${show.rating?.average}" )
            } else {
                Text("Rating: No rate")
            }
        }
    }
}