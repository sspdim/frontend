package com.example.sspdim.network

import com.squareup.moshi.Json

data class PrekeysRequest(
    @Json(name = "username") val username: String,
    @Json(name = "prekeys") val prekeys: List<ByteArray>
)