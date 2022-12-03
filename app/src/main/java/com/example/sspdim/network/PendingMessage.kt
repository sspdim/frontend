package com.example.sspdim.network

import com.squareup.moshi.Json

class PendingMessage (
    @Json(name = "from_username") val fromUsername: String,
    @Json(name = "message_content") val messageContent: String,
    @Json(name = "message_id") val messageId: String,
    @Json(name = "timestamp") val timestamp: String
)