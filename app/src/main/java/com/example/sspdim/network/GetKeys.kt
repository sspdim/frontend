package com.example.sspdim.network

import com.squareup.moshi.Json

data class GetKeys(
    @Json(name = "username") val username: String
)