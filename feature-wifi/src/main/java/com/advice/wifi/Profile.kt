package com.advice.wifi

sealed class Profile(val username: String, val password: String) {
    data object AllowAny : Profile("allowany", "allowany")
    data object LocalOnly : Profile("37c3", "37c3")
    data object OutboundOnly : Profile("outboundonly", "outboundonly")
    data object Custom : Profile("", "")
}
