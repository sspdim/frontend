package com.example.sspdim.model

import android.util.Log
import com.example.sspdim.network.GetKeys
import com.example.sspdim.network.Response2
import com.example.sspdim.network.SspdimApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.SessionBuilder
import org.whispersystems.libsignal.SessionCipher
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.protocol.CiphertextMessage
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore

class SessionModel(username: String) {
    private var status: Int = 0
    private var message: String = ""
    private var response: Response2? = null
    private var username : String = username

    private var registrationId : Int = 0
    private lateinit var identityKeyPair : ByteArray
    private lateinit var signedPreKey : ByteArray
    private lateinit var preKey : ByteArray
//    private lateinit var keys : List<PreKeyRecord>

    private lateinit var store : InMemorySignalProtocolStore
    private var address : SignalProtocolAddress = SignalProtocolAddress(username, 1)
    private lateinit var sessionBuilder : SessionBuilder

    fun session() {
//        preKey.forEach { key -> keys.add(PreKeyRecord(key)) }
        Log.d("SMD", "SADs")
        var preKey = PreKeyRecord(preKey)
        var signedPreKey = SignedPreKeyRecord(signedPreKey)
        var identityKeyPair = IdentityKeyPair(identityKeyPair)
        store = InMemorySignalProtocolStore(identityKeyPair, 9017)
        Log.d("SMD", "SADa")
        sessionBuilder = SessionBuilder(store, address)
        var preKeyBundle = PreKeyBundle(registrationId, address.deviceId, preKey.id, preKey.keyPair.publicKey, signedPreKey.id, signedPreKey.keyPair.publicKey, signedPreKey.signature, identityKeyPair.publicKey)
        Log.d("SMD", "SAD")
        sessionBuilder.process(preKeyBundle)
    }

    fun getKeysDetails() {
        resetStatus()
        val request = GetKeys(username)
        runBlocking {
            launch {
                try {
                    response = SspdimApi.retrofitService.getKeys(request)
                    status = response!!.status
//                    message = response!!.message
                    registrationId = response!!.registrationid
                    identityKeyPair = response!!.identitykeypair
                    signedPreKey = response!!.signedprekey
                    preKey = response!!.prekey
                    Log.d("SMD", "$status")
                    Log.d("SMD", "s ${response!!.signedprekey}")
                }
                catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("SMDE", "$e")
                }
            }
        }
    }

    fun encrypt(message: ByteArray): ByteArray? {
        getKeysDetails()
        Log.d("SMD", "CAlled")
        session()
        Log.d("SMD", "Called")
        var sessionCipher : SessionCipher = SessionCipher(store, address)
        var encryptedMessage : CiphertextMessage = sessionCipher.encrypt(message)
        Log.d("SMD", "EM ${encryptedMessage.serialize()}")
        return encryptedMessage.serialize()
    }

    private fun resetStatus() {
        status = 0
        message = ""
    }
}