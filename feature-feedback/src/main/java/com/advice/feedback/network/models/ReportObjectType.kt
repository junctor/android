package com.advice.feedback.network.models

import com.google.gson.annotations.SerializedName

enum class ReportObjectType {
    @SerializedName("content")
    CONTENT,

    @SerializedName("org")
    ORG,

    @SerializedName("person")
    PERSON,

    @SerializedName("document")
    DOCUMENT,
}
