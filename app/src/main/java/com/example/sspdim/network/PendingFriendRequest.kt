package com.example.sspdim.network

import com.squareup.moshi.Json

data class PendingFriendRequest (
    @Json(name = "from_username") val fromUsername: String,
    @Json(name = "status") val status: Int
)