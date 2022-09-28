package com.example.sspdim.network

import com.squareup.moshi.Json

data class Response (
    @Json(name = "status") val status: Int,
    @Json(name = "message") val message: String
) {
    companion object {
        const val STATUS_SUCCESS = 200
        const val STATUS_CLIENT_ERROR = 400
        const val STATUS_SERVER_ERROR = 500
    }
}