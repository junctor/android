package com.advice.firebase.models.menu

data class FirebaseMenuItem(
    val applied_tag_ids: List<Long> = emptyList(),
    val document_id: Long? = null,
    val function: String = "",
    val google_materialsymbol: String = "",
    val id: Long = -1,
    val menu_id: Long? = null,
    val prohibit_tag_filter: String = "",
    val sort_order: Int = -1,
    val title_text: String = "",
    val description: String? = null,
)
