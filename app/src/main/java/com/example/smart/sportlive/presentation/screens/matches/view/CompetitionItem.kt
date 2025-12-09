package com.example.smart.sportlive.presentation.screens.matches.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smart.sportlive.domain.model.Competition

@Composable
fun CompetitionItem(competition: Competition) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = competition.name,
            modifier = Modifier.padding(12.dp)
        )
    }
}

