package com.advice.firebase.models.products

import com.google.firebase.firestore.PropertyName

data class FirebaseProductMedia(
    @get:PropertyName("url")
    @set:PropertyName("url")
    var url: String = "",
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = -1,
    @get:PropertyName("hash_sha256")
    @set:PropertyName("hash_sha256")
    var hashSha256: String = "",
    @get:PropertyName("filetype")
    @set:PropertyName("filetype")
    var filetype: String = "",
    @get:PropertyName("product_id")
    @set:PropertyName("product_id")
    var productId: Long = -1L,
    @get:PropertyName("hash_md5")
    @set:PropertyName("hash_md5")
    var hashMd5: String = "",
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("filesize")
    @set:PropertyName("filesize")
    var filesize: Long = -1L,
    @get:PropertyName("asset_id")
    @set:PropertyName("asset_id")
    var assetId: Long = -1L,
)
