package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseMedia(
    @get:PropertyName("asset_id")
    @set:PropertyName("asset_id")
    var assetId: Long = -1L,
    @get:PropertyName("filesize")
    @set:PropertyName("filesize")
    var filesize: Long = -1L,
    @get:PropertyName("filetype")
    @set:PropertyName("filetype")
    var filetype: String = "",
    @get:PropertyName("hash_crc32c")
    @set:PropertyName("hash_crc32c")
    var hashCrc32c: String = "",
    @get:PropertyName("hash_md5")
    @set:PropertyName("hash_md5")
    var hashMd5: String = "",
    @get:PropertyName("hash_sha256")
    @set:PropertyName("hash_sha256")
    var hashSha256: String = "",
    @get:PropertyName("is_logo")
    @set:PropertyName("is_logo")
    var isLogo: String = "N",
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = 0,
    @get:PropertyName("url")
    @set:PropertyName("url")
    var url: String = "",
) : Parcelable
