package com.advice.firebase.models.menu

import com.google.firebase.firestore.PropertyName

data class FirebaseMenuItem(
    @get:PropertyName("applied_tag_ids")
    @set:PropertyName("applied_tag_ids")
    var appliedTagIds: List<Long> = emptyList(),
    @get:PropertyName("document_id")
    @set:PropertyName("document_id")
    var documentId: Long? = null,
    @get:PropertyName("function")
    @set:PropertyName("function")
    var function: String = "",
    @get:PropertyName("google_materialsymbol")
    @set:PropertyName("google_materialsymbol")
    var googleMaterialsymbol: String = "",
    @get:PropertyName("apple_sfsymbol")
    @set:PropertyName("apple_sfsymbol")
    var appleSfsymbol: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("menu_id")
    @set:PropertyName("menu_id")
    var menuId: Long? = null,
    @get:PropertyName("prohibit_tag_filter")
    @set:PropertyName("prohibit_tag_filter")
    var prohibitTagFilter: String = "",
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = -1,
    @get:PropertyName("title_text")
    @set:PropertyName("title_text")
    var titleText: String = "",
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String? = null,
)
