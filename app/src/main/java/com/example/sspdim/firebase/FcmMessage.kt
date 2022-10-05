package com.example.sspdim.firebase

import com.squareup.moshi.Json

data class FcmMessage (
    @Json(name = "action") val action: String,
    @Json(name = "data") val data: String
)