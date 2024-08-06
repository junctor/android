package com.advice.glitch.ui

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.advice.glitch.R
import java.util.Random

@Composable
fun SoundButton() {
    val context = LocalContext.current
    // val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    Image(
        painter = painterResource(id = R.drawable.chicken),
        contentDescription = "rubber chicken lmao",
        modifier = Modifier
            .clip(RoundedCornerShape(50f))
            .clickable {
                // todo: maybe don't - setVolume(audioManager)
                playRandomChickenNoise(context)
            }
    )
}

private fun setVolume(
    audioManager: AudioManager,
) {
    audioManager.setStreamVolume(
        AudioManager.STREAM_MUSIC,
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
        0
    )
}

private fun playRandomChickenNoise(
    context: Context
) {
    val sounds = listOf(
        R.raw.rubber_1,
        R.raw.rubber_2,
        R.raw.rubber_3,
        R.raw.rubber_4,
        R.raw.rubber_5
    )
    val randomSound = sounds[Random().nextInt(sounds.size)]
    val mediaPlayer = MediaPlayer.create(context, randomSound)
    mediaPlayer.start()
}


@PreviewLightDark
@Composable
fun SoundButtonPreview() {
    SoundButton()
}
