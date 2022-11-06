package com.example.sspdim.model

import android.content.Context
import android.util.Log
import com.example.sspdim.database.AppDatabase
import com.example.sspdim.database.Keys
import com.example.sspdim.network.KeysRequest
import com.example.sspdim.network.PrekeysRequest
import com.example.sspdim.network.Response
import com.example.sspdim.network.SspdimApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.util.KeyHelper

class KeysModel(username : String) {

    constructor(context1: Context, username: String) : this(username) {
        context = context1
    }

    private lateinit var context: Context

    private var status: Int = 0
    private var message: String = ""
    private var response: Response? = null
    private var username : String = username

    var identityKeyPair : IdentityKeyPair = KeyHelper.generateIdentityKeyPair()
    var registrationId : Int = KeyHelper.generateRegistrationId(false)
    var preKeys : List<PreKeyRecord> = KeyHelper.generatePreKeys(1, 100)
    var signedPreKey : SignedPreKeyRecord = KeyHelper.generateSignedPreKey(identityKeyPair, 5)

    var database = AppDatabase.getDatabase(context)
    var keys : ArrayList<ByteArray> = ArrayList()

    fun addKeys() {
        preKeys.forEach{key -> keys.add(key.serialize())}
        var keys = Keys(identityKeyPair.serialize(), registrationId, signedPreKey.serialize())
        runBlocking {
            try {
                database.keysDao().insert(keys)
                Log.d("Keys", "$keys")
            }
            catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun submitKeysDetails(): Int {
        resetStatus()
        val request = KeysRequest(username, identityKeyPair.serialize(), registrationId, keys, signedPreKey.serialize())
        runBlocking {
            launch {
                try {
                    response = SspdimApi.retrofitService.submitKeys(request)
                    status = response!!.status
                    message = response!!.message
                    Log.d("SKD", "$status")
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return status
    }

    fun submitPrekeys() {
        preKeys = KeyHelper.generatePreKeys(1, 100)
        preKeys.forEach{key -> keys.add(key.serialize())}
        val request = PrekeysRequest(username, keys)
        runBlocking {
            launch {
                try {
                    response = SspdimApi.retrofitService.submitPrekeys(request)
                    status = response!!.status
                    message = response!!.message
                    Log.d("SKD", "$status")
                }
                catch (e: Exception) {
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