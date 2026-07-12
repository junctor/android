package com.advice.core.local

data class User(val id: String, val ageInfo: AgeSignalsInfo)
fun User.canView(minAge: Int?) = ageInfo.canView(minAge)
