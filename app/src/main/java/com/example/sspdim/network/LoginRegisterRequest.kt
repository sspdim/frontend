package com.example.sspdim.network

import com.squareup.moshi.Json

data class LoginRegisterRequest (
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String
)