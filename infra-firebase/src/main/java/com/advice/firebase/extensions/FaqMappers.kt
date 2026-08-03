package com.advice.firebase.extensions

import com.advice.core.local.FAQ
import com.advice.firebase.models.FirebaseFAQ

fun FirebaseFAQ.toFAQ() = FAQ(question, answer)
