package com.example.sspdim.network

import com.squareup.moshi.Json

data class GetPendingFriendRequestsRequest (
    @Json(name = "username") val username: String
)