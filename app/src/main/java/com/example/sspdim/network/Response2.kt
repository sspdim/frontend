package com.example.sspdim.network

import com.squareup.moshi.Json

data class Response2 (
    @Json(name = "status") val status: Int,
    @Json(name = "registrationid") val registrationid: Int,
    @Json(name = "identitykeypair") val identitykeypair: ByteArray,
    @Json(name = "prekey") val prekey: ByteArray,
    @Json(name = "signedprekey") val signedprekey: ByteArray,
    @Json(name = "number_of_prekeys") val numberOfPrekeys: Int
)
