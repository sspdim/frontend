package com.example.sspdim.network

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

private const val TAG = "SspdimApiService"

private var BASE_URL = "https://capstone1.devmashru.tech"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private var retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

object SspdimApi {
    var retrofitService: SspdimApiService = retrofit.create(SspdimApiService::class.java)
}

fun setBaseUrl(url: String) {
    BASE_URL = url
    Log.d(TAG, "Base url: $BASE_URL")
    retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()
    SspdimApi.retrofitService = retrofit.create(SspdimApiService::class.java)
    Log.d("$TAG new", retrofit.baseUrl().toString())
}

interface SspdimApiService {

    @GET("get-servers-list")
    suspend fun getServersList(): List<Server>

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun submitLogin(@Body requestData: LoginRegisterRequest): Response

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun submitRegister(@Body requestData: LoginRegisterRequest): Response

    @Headers("Content-Type: application/json")
    @POST("add-token")
    suspend fun addToken(@Body requestData: AddFirebaseTokenRequest): Response

    @Headers("Content-Type: application/json")
    @POST("send-message")
    suspend fun sendMessage(@Body requestData: SendMessageRequest): Response

    @Headers("Content-Type: application/json")
    @POST("add-friend")
    suspend fun addFriend(@Body requestData: AddFriendRequest): Response

    @Headers("Content-Type: application/json")
    @POST("accept-friend")
    suspend fun acceptFriend(@Body requestData: AddFriendRequest): Response

    @Headers("Content-Type: application/json")
    @POST("pending-friend-requests")
    suspend fun getPendingFriendRequests(@Body requestData: GetPendingFriendRequestsRequest): List<PendingFriendRequest>

    @Headers("Content-Type: application/json")
    @POST("pending-messages")
    suspend fun getPendingMessages(@Body requestData: GetPendingMessagesRequest): List<PendingMessage>

    @Headers("Content-Type: application/json")
    @POST("keys")
    suspend fun submitKeys(@Body requestData: KeysRequest): Response
}

