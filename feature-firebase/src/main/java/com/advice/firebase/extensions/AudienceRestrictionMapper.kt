package com.advice.firebase.extensions

import com.advice.core.audience.AudienceRestriction
import com.advice.firebase.models.FirebaseContent
import com.advice.firebase.models.FirebaseDocument
import com.advice.firebase.models.FirebaseSpeaker
import com.advice.firebase.models.organization.FirebaseOrganization
import com.advice.firebase.models.products.FirebaseProduct

val FirebaseContent.audienceRestriction: AudienceRestriction
    get() = AudienceRestriction(minimumAge = visibleAgeMin)

val FirebaseContent.audienceLabel: String
    get() = title

val FirebaseSpeaker.audienceRestriction: AudienceRestriction
    get() = AudienceRestriction(minimumAge = visibleAgeMin)

val FirebaseSpeaker.audienceLabel: String
    get() = name

val FirebaseDocument.audienceRestriction: AudienceRestriction
    get() = AudienceRestriction(minimumAge = visibleAgeMin)

val FirebaseDocument.audienceLabel: String
    get() = titleText

val FirebaseOrganization.audienceRestriction: AudienceRestriction
    get() = AudienceRestriction(minimumAge = visibleAgeMin)

val FirebaseOrganization.audienceLabel: String
    get() = name

val FirebaseProduct.audienceRestriction: AudienceRestriction
    get() = AudienceRestriction(minimumAge = visibleAgeMin)

val FirebaseProduct.audienceLabel: String
    get() = title
