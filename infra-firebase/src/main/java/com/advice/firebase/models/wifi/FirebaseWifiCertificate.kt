package com.advice.firebase.models.wifi

import com.google.firebase.firestore.PropertyName

data class FirebaseWifiCertificate(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("name_text")
    @set:PropertyName("name_text")
    var nameText: String = "",
    @get:PropertyName("crt_url")
    @set:PropertyName("crt_url")
    var crtUrl: String = "",
    @get:PropertyName("pem_url")
    @set:PropertyName("pem_url")
    var pemUrl: String = "",
)
