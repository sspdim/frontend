package com.example.sspdim.network

import com.squareup.moshi.Json

data class SendMessageRequest (
    @Json(name = "f") val from: String,
    @Json(name = "to") val to: String,
    @Json(name = "message") val message: String
)