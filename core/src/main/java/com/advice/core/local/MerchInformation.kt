package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MerchInformation(
    val merchHelpDocId: Long?,
    val merchMandatoryAcknowledgement: String? = null,
    val merchTaxStatement: String? = null,
) : Parcelable
