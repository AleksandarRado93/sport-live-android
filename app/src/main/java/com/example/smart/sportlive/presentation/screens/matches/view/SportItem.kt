package com.example.smart.sportlive.presentation.screens.matches.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smart.sportlive.domain.model.Sport

@Composable
fun SportItem(sport: Sport) {
    Card(
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = sport.name,
            modifier = Modifier.padding(12.dp)
        )
    }
}

