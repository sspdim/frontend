package com.example.sspdim.network

import com.squareup.moshi.Json

data class SendMessageRequest (
    @Json(name = "from") val from: String,
    @Json(name = "to") val to: String,
    @Json(name = "message") val message: String,
    @Json(name = "message_id") val messageId: String,
    @Json(name = "timestamp") val timestamp: String
)