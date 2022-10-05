package com.example.sspdim.network

import com.squareup.moshi.Json

data class AddFriendRequest (
    @Json(name = "username") val username: String,
    @Json(name = "friend_username") val friendUsername: String
)