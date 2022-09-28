package com.example.sspdim.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.sspdim.network.LoginRegisterRequest
import com.example.sspdim.network.Response
import com.example.sspdim.network.SspdimApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

private const val TAG = "LoginRegisterViewModel"

class LoginRegisterViewModel: ViewModel() {
    var username: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var status: Int = 0
    var message: String = ""
    var response: Response? = null

    fun initData(username: String, password: String) {
        this.username = username
        this.password = password
    }

    fun submitLoginDetails() {
        resetStatus()
        val request = LoginRegisterRequest(username, password)
        runBlocking {
            launch {
                try {
                    Log.d(TAG, "sending request")
                    response = SspdimApi.retrofitService.submitLogin(request)
                    status = response!!.status
                    message = response!!.message
                    Log.d(TAG, "received response")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun submitRegisterDetails() {
        resetStatus()
        val request = LoginRegisterRequest(username, password)
        runBlocking {
            launch {
                try {
                    Log.d(TAG, "sending request")
                    response = SspdimApi.retrofitService.submitRegister(request)
                    status = response!!.status
                    message = response!!.message
                    Log.d(TAG, "received response")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun resetStatus() {
        status = 0
        message = ""
    }
}