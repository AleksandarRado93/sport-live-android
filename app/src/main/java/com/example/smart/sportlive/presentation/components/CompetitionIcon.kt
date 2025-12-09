package com.example.smart.sportlive.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun CompetitionIcon(iconUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        contentScale = ContentScale.Fit
    )
}

