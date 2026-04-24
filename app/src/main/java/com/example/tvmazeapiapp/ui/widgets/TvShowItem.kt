package com.example.tvmazeapiapp.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tvmazeapiapp.R
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.ui.theme.cardBorder

@Composable
fun TvShowItem(
    show: TvShow,
    onClick: () -> Unit,
    onToggleFavorite: (TvShow) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
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
                    Text(text = "Rating: ${show.rating?.average}")
                } else {
                    Text("Rating: No rate")
                }
            }

            Button(
                onClick = { onToggleFavorite(show) },
                modifier = Modifier.size(width = 100.dp, height = 40.dp)
            ) {
                Text(if (show.isFavorite) "+" else "-")
            }
        }
    }
}