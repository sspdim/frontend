package com.example.sspdim.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sspdim.network.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.Exception

class RegisterViewModel: ViewModel() {
    var username: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var status: Int = 0
    var message: String = ""
    var response: Response? = null

    private val _servers = MutableLiveData<List<Server>>()
    val servers: LiveData<List<Server>> = _servers

    var selectedServerDomainName: String = ""

    fun initData(username: String, password: String, confirmPassword: String) {
        this.username = username
        this.password = password
        this.confirmPassword = confirmPassword
    }

    fun submitRegisterDetails(): String {
        resetStatus()
        Log.d("sdvsd", "$username, $password")
        val request = LoginRegisterRequest(username, password)
        runBlocking {
            launch {
                try {
                    response = SspdimApi.retrofitService.submitRegister(request)
                    status = response!!.status
                    message = response!!.message
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return username
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

    fun setBaseDomain() {
        setBaseUrl("https://$selectedServerDomainName")
    }
}