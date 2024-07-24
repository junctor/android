package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseMedia(
    @PropertyName("asset_id")
    val asset_id: Long = -1L,
    @PropertyName("filesize")
    val filesize: Long = -1L,
    @PropertyName("filetype")
    val filetype: String = "",
    @PropertyName("hash_crc32c")
    val hash_crc32c: String = "",
    @PropertyName("hash_md5")
    val hash_md5: String = "",
    @PropertyName("hash_sha256")
    val hash_sha256: String = "",
    @PropertyName("is_logo")
    val logo: String = "N",
    @PropertyName("name")
    val name: String = "",
    @PropertyName("sort_order")
    val sort_order: Int = 0,
    @PropertyName("url")
    val url: String = "",
) : Parcelable
