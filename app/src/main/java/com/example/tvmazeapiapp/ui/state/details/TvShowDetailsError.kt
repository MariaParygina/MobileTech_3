package com.example.tvmazeapiapp.ui.state.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tvmazeapiapp.ui.theme.cardBorder
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsState

@Composable
fun TvShowDetailsError(
    id: Int,
    state: TvShowDetailsState.Error,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Error: ${state.message}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, cardBorder),
                onClick = onRetry
            ) {
                Text("Retry")
            }

            Button(
                onClick = onBackClick,
                border = BorderStroke(1.dp, cardBorder),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE1E0E7),
                    contentColor = Color.Black
                )
            ) {
                Text("Go Back")
            }
        }
    }
}