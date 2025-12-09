package com.example.smart.sportlive.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.smart.sportlive.R
import com.example.smart.sportlive.presentation.ui.theme.TextPrimary

@Composable
fun ErrorContent(
    message: String? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message ?: stringResource(R.string.error_load_data),
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
    }
}
