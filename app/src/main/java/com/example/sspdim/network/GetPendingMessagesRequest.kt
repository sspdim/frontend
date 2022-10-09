package com.example.sspdim.network

import com.squareup.moshi.Json

class GetPendingMessagesRequest (
    @Json(name = "username") val username: String
)