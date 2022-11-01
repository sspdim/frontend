package com.example.sspdim.network

import com.squareup.moshi.Json

data class KeysRequest(
    @Json(name = "username") val username: String,
    @Json(name = "identitykeypair") val identitykeypair: ByteArray,
    @Json(name = "registrationid") val registrationid: Int,
    @Json(name = "prekeys") val prekeys: List<ByteArray>,
    @Json(name = "signedprekey") val signedprekey: ByteArray
)