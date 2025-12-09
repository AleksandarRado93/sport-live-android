package com.example.smart.sportlive.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smart.sportlive.presentation.ui.theme.GoldAccent
import com.example.smart.sportlive.presentation.ui.theme.TextPrimary
import com.example.smart.sportlive.presentation.ui.theme.spacing

@Composable
fun SectionHeader(title: String) {
    val spacing = MaterialTheme.spacing
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(spacing.extraSmall)
                .height(spacing.large)
                .background(GoldAccent, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(spacing.small))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}

