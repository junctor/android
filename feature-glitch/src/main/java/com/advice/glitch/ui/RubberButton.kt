package com.advice.glitch.ui

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.advice.glitch.R
import java.util.Random

@Composable
fun SoundButton() {
    val context = LocalContext.current
    // val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    Image(
        painter = painterResource(id = R.drawable.chicken),
        contentDescription = stringResource(R.string.cd_rubber_chicken),
        modifier =
            Modifier
                .clip(RoundedCornerShape(50f))
                .semantics { role = Role.Button }
                .clickable {
                    // todo: maybe don't - setVolume(audioManager)
                    playRandomChickenNoise(context)
                },
    )
}

private fun playRandomChickenNoise(context: Context) {
    val sounds =
        listOf(
            R.raw.rubber_1,
            R.raw.rubber_2,
            R.raw.rubber_3,
            R.raw.rubber_4,
            R.raw.rubber_5,
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
