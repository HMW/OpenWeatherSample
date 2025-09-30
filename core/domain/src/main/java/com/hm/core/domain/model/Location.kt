package com.hm.core.domain.model

data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val state: String? = null,
    val countryCode: String? = null
)



