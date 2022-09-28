package com.example.sspdim.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

private const val BASE_URL = "https://capstone.devmashru.tech"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

object SspdimApi {
    val retrofitService: SspdimApiService by lazy {
        retrofit.create(SspdimApiService::class.java)
    }
}

interface SspdimApiService {

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun submitLogin(@Body requestData: LoginRegisterRequest): Response

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun submitRegister(@Body requestData: LoginRegisterRequest): Response
}

