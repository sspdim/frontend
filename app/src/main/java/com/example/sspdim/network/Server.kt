package com.example.sspdim.network

import com.squareup.moshi.Json

data class Server (
    @Json(name = "domain_name") val domainName: String,
    @Json(name = "ip_address") val ipAddress: String
)