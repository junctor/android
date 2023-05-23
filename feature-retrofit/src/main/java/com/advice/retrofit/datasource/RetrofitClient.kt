package com.advice.retrofit.datasource

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class Article(
    val text: StringValue,
    val name: StringValue,
    val conference: StringValue,
)

data class ApiResponse<DocumentType>(
    val documents: List<Document<DocumentType>>
)

data class Document<DocumentType>(
    val name: String,
    val fields: DocumentType
)

data class StringValue(
    val stringValue: String
)

interface RetrofitService {

    @GET("{conference}/articles")
    suspend fun getArticles(@Path("conference") conference: String): ApiResponse<Article>
}


class RetrofitClient {

    private val service: RetrofitService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://firestore.googleapis.com/v1/projects/hackertest-5a202/databases/(default)/documents/conferences/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(RetrofitService::class.java)

    }

    suspend fun get(conference: String): ApiResponse<Article> {
        return service.getArticles(conference)
    }
}