package com.advice.retrofit.datasource

import kotlinx.coroutines.runBlocking
import org.junit.Test

class RetrofitClientTest {

    @Test
    fun get() = runBlocking {
        val client = RetrofitClient()

        val result = client.get("THOTCON0xC")

        println(result.documents.first().fields.text)
    }
}
