package com.example.smart.sportlive.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.smart.sportlive.R
import com.example.smart.sportlive.presentation.ui.theme.GoldAccent
import com.example.smart.sportlive.presentation.ui.theme.TextPrimary
import com.example.smart.sportlive.presentation.ui.theme.spacing

@Composable
fun ErrorContent(
    message: String? = null,
    onRetry: (() -> Unit)? = null
) {
    val spacing = MaterialTheme.spacing
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message ?: stringResource(R.string.error_load_data),
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
        
        onRetry?.let { retry ->
            Spacer(modifier = Modifier.height(spacing.default))
            Button(
                onClick = retry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent
                )
            ) {
                Text(
                    text = stringResource(R.string.retry_button),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
