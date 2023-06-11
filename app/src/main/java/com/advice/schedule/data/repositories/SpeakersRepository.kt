package com.advice.schedule.data.repositories

import com.advice.data.sources.SpeakersDataSource

class SpeakersRepository(
    private val speakersDataSource: SpeakersDataSource,
) {

    val list = speakersDataSource.get()

}