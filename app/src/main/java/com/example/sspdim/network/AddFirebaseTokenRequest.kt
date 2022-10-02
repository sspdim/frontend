package com.example.sspdim.network

import com.squareup.moshi.Json

data class AddFirebaseTokenRequest (
    @Json(name = "username") val username: String,
    @Json(name = "token") val token: String
)