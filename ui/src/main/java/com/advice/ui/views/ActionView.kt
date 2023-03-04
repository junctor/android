package com.advice.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme

@Composable
fun ActionView(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {

            }
            .padding(16.dp)
    ) {
        Icon(Icons.Default.Star, null)
        Spacer(Modifier.width(4.dp))
        Text(text, Modifier.fillMaxWidth())
    }
}

private fun getResource(url: String): Int {
//        if (url.contains("discord.com") || url.contains("discordapp.com"))
//            return R.drawable.ic_discord_logo_white
//        if (url.contains("twitter.com"))
//            return R.drawable.ic_twitter
//
//        if (url.contains("forum.defcon.org"))
//            return R.drawable.ic_baseline_forum_24
//
//        if (url.contains("twitch.tv"))
//            return R.drawable.ic_glitch_white_rgb
//
//        if (url.contains("youtube.com"))
//            return R.drawable.youtube_social_icon_red
//
//        if (url.contains("soundcloud.com"))
//            return R.drawable.soundcloud
//
//        if (url.contains("facebook.com"))
//            return R.drawable.logo_facebook
//
//        return R.drawable.browser
    return -1
}

private fun getLabel(url: String): String {
    val temp = if (url.endsWith("/")) {
        url.substring(0, url.length - 1)
    } else {
        url
    }

    val substring = temp.substring(temp.lastIndexOf("/") + 1)

    if (url.contains("discord.com") || url.contains("discordapp.com"))
        return "discordapp.com"

    if (url.contains("twitter.com"))
        return "@$substring"

    if (url.contains("forum.defcon.org"))
        return "forum.defcon.org"

    if (url.contains("twitch.tv"))
        return "twitch.tv/$substring"

    if (url.contains("youtube.com"))
        return "youtube.com"

    if (url.contains("soundcloud.com"))
        return "@$substring"

    if (url.contains("facebook.com"))
        return "@$substring"

    return url
}

@Preview(showBackground = true)
@Composable
fun ActionViewPreview() {
    ScheduleTheme {
        ActionView("Discord.com")
    }
}