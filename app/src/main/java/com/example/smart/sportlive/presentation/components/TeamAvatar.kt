package com.example.smart.sportlive.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.smart.sportlive.presentation.ui.theme.DarkSurface

@Composable
fun TeamAvatar(
    avatarUrl: String?,
    size: Int = 32
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(DarkSurface),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(size.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

