package com.advice.firebase.models.menu

import com.google.firebase.firestore.PropertyName

data class FirebaseMenuItem(
    val applied_tag_ids: List<Int> = emptyList(),
    @get:PropertyName("document_id") @set:PropertyName("document_id") var document: Int? = null,
    val function: String = "",
    val google_materialsymbol: String = "",
    val id: Long = -1,
    val menu_id: Int? = null,
    val prohibit_tag_filter: String = "",
    val sort_order: Int = -1,
    val title_text: String = "",
    val description: String? = null,
)
