package com.advice.schedule.repository

import com.advice.data.datasource.SpeakersDataSource

class SpeakersRepository(
    private val speakersDataSource: SpeakersDataSource,
) {

    val list = speakersDataSource.get()

}