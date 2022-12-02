package com.example.sspdim.crypto

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.sspdim.database.Converters
import com.example.sspdim.database.Keys
import com.example.sspdim.database.KeysDatabase
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

class KeysModel(context: Context, username : String) {

    private var status: Int = 0
    private var message: String = ""
    private var response: Response? = null
    private var username : String = username

    var identityKeyPair : IdentityKeyPair = KeyHelper.generateIdentityKeyPair()
    var registrationId : Int = KeyHelper.generateRegistrationId(false)
    var preKeys : List<PreKeyRecord> = KeyHelper.generatePreKeys(1, 100)
    var signedPreKey : SignedPreKeyRecord = KeyHelper.generateSignedPreKey(identityKeyPair, 5)

    var database = KeysDatabase.getKeyDatabase(context)
    var keys : ArrayList<ByteArray> = ArrayList()
    var keysAsString : ArrayList<String> = ArrayList()

    fun addKeys() {
        preKeys.forEach{key -> keys.add(key.serialize())}
        preKeys.forEach{key -> keysAsString.add(Base64.encodeToString(key.serialize(), Base64.DEFAULT))}
        var keys = Keys(identityKeyPair.serialize(), registrationId, keysAsString, signedPreKey.serialize())
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

    fun addPrekeys() {
        preKeys = KeyHelper.generatePreKeys(1, 100)
        preKeys.forEach { key -> keysAsString.add(key.serialize().toString()) }
        runBlocking {
            try {
                val converter = Converters()
                val currentPrekeys = converter.jsonToList(database.keysDao().getPrekeys())
                currentPrekeys.forEach { key -> keysAsString.add(key) }
                database.keysDao().updatePrekeys(keysAsString)
                Log.d("Keys", "$keys")
            }
            catch (e : Exception) {
                e.printStackTrace()
            }
        }
        submitPrekeys(preKeys)
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

    fun submitPrekeys(preKeys: List<PreKeyRecord>) {
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

    fun getPrekeys(): ArrayList<ByteArray> {
        val converter = Converters()
        val preKeys = converter.jsonToList(database.keysDao().getPrekeys())
        preKeys.forEach { key -> keys.add(Base64.decode(key, Base64.DEFAULT))}
        return keys
    }

    suspend fun deleteAllKeys() {
        database.keysDao().deleteAllKeys()
    }
}