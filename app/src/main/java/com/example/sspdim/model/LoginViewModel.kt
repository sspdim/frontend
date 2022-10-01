package com.example.sspdim.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sspdim.network.LoginRegisterRequest
import com.example.sspdim.network.Response
import com.example.sspdim.network.Server
import com.example.sspdim.network.SspdimApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.Exception

class LoginViewModel: ViewModel() {
    var username: String = ""
    var password: String = ""
    var status: Int = 0
    var message: String = ""
    var response: Response? = null

    private val _servers = MutableLiveData<List<Server>>()
    val servers: LiveData<List<Server>> = _servers

    private val _server = MutableLiveData<Server>()
    val server: LiveData<Server> = _server

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
                    response = SspdimApi.retrofitService.submitLogin(request)
                    status = response!!.status
                    message = response!!.message
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getServerDetails() {
        viewModelScope.launch {
            try {
                _servers.value = SspdimApi.retrofitService.getServersList()
            }
            catch (e: Exception) {
                e.printStackTrace()
                _servers.value = listOf()
            }
        }
    }

    private fun resetStatus() {
        status = 0
        message = ""
    }
}