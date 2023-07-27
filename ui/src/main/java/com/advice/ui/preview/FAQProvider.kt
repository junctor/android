package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.FAQ

class FAQProvider : PreviewParameterProvider<FAQ> {

    private val list = listOf(
        FAQ(
            "What is DEF CON doing for DC30, and how can I attend?",
            "DEF CON 30 will be a semi-hybrid event this year, we will give hackers a choice in how they wish to experience DEF CON but we are returning at full operating capacity. What do we mean by semi-hybrid? We will be hosting our full con in-person in Las Vegas and our approved villages and contests will be contributing additional online content within the official DEF CON Discord. All Online content will be similar to the 2020 & 2021 cons. Our official talks will be streamed via DCTV on our Twitch, and several contests and villages will be providing unique online immersive contests and presentations.To see what happenings are currently planning to be in-person, hybrid, or virtual only please visit https://forum.defcon.org/node/239768."
        ),
        FAQ(
            "Can I buy a DEF CON badge with Black Hat?",
            "Yes, it will be an option when you check out at Black Hat."
        ),
        FAQ(
            "What health measures/protocols is DEF CON taking to ensure a safe environment on-site?",
            "DEF CON is working closely with Caesars Entertainment hotels to provide a safe and healthy experience for all. We will comply with whatever safety measures are required of us."
        ),
    )

    override val values: Sequence<FAQ>
        get() = list.asSequence()
}
