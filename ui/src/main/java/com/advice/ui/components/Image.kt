package com.advice.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.advice.glitch.ui.GlitchLogo
import com.advice.ui.R
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
    val request =
        ImageRequest
            .Builder(LocalContext.current)
            .data(model)
            .crossfade(enable = true)
            .build()

    Image(request, contentDescription, modifier, contentScale)
}

@Composable
fun Image(
    request: ImageRequest,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    Box(
        modifier =
            modifier
                .clip(roundedCornerShape)
                .background(Color.Black),
    ) {
        SubcomposeAsyncImage(
            model = request,
            imageLoader = LocalContext.current.getImageLoader(LocalInspectionMode.current),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Image(
                    painter = painterResource(id = R.drawable.logo_clean),
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
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
