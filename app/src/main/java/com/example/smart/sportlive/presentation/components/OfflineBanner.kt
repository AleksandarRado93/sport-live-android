package com.example.smart.sportlive.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.smart.sportlive.R
import com.example.smart.sportlive.presentation.ui.theme.TextPrimary
import com.example.smart.sportlive.presentation.ui.theme.spacing

@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
            .padding(horizontal = spacing.default, vertical = spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(spacing.large)
        )
        Spacer(modifier = Modifier.width(spacing.small))
        Text(
            text = stringResource(R.string.offline_mode),
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

