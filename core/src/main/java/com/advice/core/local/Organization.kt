package com.advice.core.local

data class Organization(
    val id: Long,
    val name: String,
    val description: String?,
    val locations: List<OrganizationLocation>,
    val links: List<OrganizationLink>,
    val tags: List<Long>,
)

data class OrganizationLocation(
    val id: Long,
)

data class OrganizationLink(
    val label: String,
    val type: String,
    val url: String,
)
