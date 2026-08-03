package com.advice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.advice.ui.glitch.GlitchLogo
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.roundedCornerShape
import com.advice.ui.utils.getImageLoader

@Composable
fun Image(
    model: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val context = LocalContext.current
    val request =
        remember(model) {
            ImageRequest
                .Builder(context)
                .data(model)
                .memoryCacheKey(model)
                .diskCacheKey(model)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(enable = true)
                .build()
        }

    Image(request, contentDescription, modifier, contentScale)
}

@Composable
fun Image(
    request: ImageRequest,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val imageLoader = remember(isPreview) { context.getImageLoader(isPreview) }

    Box(
        modifier =
            modifier
                .clip(roundedCornerShape)
                .background(Color.Black),
    ) {
        SubcomposeAsyncImage(
            model = request,
            imageLoader = imageLoader,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
            loading = {
                // Keep the black card background while decoding; avoid flashing the HT logo
                // on LazyColumn recycle when the bitmap is already in memory cache.
            },
            error = {
                GlitchLogo(
                    contentDescription = contentDescription,
                    animated = true,
                    modifier = Modifier.fillMaxSize(),
                )
            },
            success = {
                SubcomposeAsyncImageContent()
            },
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewImage() {
    val request =
        ImageRequest
            .Builder(LocalContext.current)
            .data("https://info.defcon.org/blobs/v_aerospace.png")
            .crossfade(enable = true)
            .build()

    Image(
        request = request,
        contentDescription = "example image",
        modifier = Modifier.size(240.dp),
        contentScale = ContentScale.Fit,
    )
}
