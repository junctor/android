package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Action(val label: String, val url: String) : Parcelable
