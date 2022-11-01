package com.example.sspdim.model

import android.util.Base64
import android.util.Log
import com.example.sspdim.network.GetKeys
import com.example.sspdim.network.Response2
import com.example.sspdim.network.SspdimApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okio.ByteString.Companion.toByteString
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.SessionBuilder
import org.whispersystems.libsignal.SessionCipher
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.protocol.CiphertextMessage
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
import java.nio.charset.StandardCharsets

class SessionModel(username: String) {
    private var status: Int = 0
    private var message: String = ""
    private var response: Response2? = null
    private var username : String = username

    private var registrationId : Int = 0
    private lateinit var identityKeyPair : ByteArray
    private lateinit var signedPreKey : ByteArray
    private lateinit var preKey : ByteArray
    private lateinit var preKeys : List<ByteArray>

    private lateinit var store : InMemorySignalProtocolStore
    private var address : SignalProtocolAddress = SignalProtocolAddress(username, 1)
    private lateinit var sessionBuilder : SessionBuilder
    private lateinit var sessionCipher : SessionCipher

    fun session() {
        Log.d("SMD", "SADs")
        var preKey = PreKeyRecord(preKey)
        var signedPreKey = SignedPreKeyRecord(signedPreKey)
        var identityKeyPair = IdentityKeyPair(identityKeyPair)
        store = InMemorySignalProtocolStore(identityKeyPair, 9017)
        store.storeSignedPreKey(signedPreKey.id, signedPreKey)
        preKeys.forEach { key -> store.storePreKey(PreKeyRecord(key).id, PreKeyRecord(key)) }
        Log.d("SMD", "SADa")
        sessionBuilder = SessionBuilder(store, address)
        var preKeyBundle = PreKeyBundle(registrationId, address.deviceId, preKey.id, preKey.keyPair.publicKey, signedPreKey.id, signedPreKey.keyPair.publicKey, signedPreKey.signature, identityKeyPair.publicKey)
        Log.d("SMD", "SAD")
        sessionBuilder.process(preKeyBundle)
        sessionCipher = SessionCipher(store, address)
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
                    preKeys = response!!.prekeys
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

    fun encrypt(message: ByteArray): String? {
        getKeysDetails()
        Log.d("SMD", "CAlled")
        session()
        Log.d("SMD", "Called")
        var encryptedMessage : CiphertextMessage = sessionCipher.encrypt(message)
        var preKeySignalmessage : PreKeySignalMessage = PreKeySignalMessage(encryptedMessage.serialize())
        Log.d("SMD", "EM ${encryptedMessage.serialize().toUByteArray()}")
        Log.d("SMD", "EM ${Base64.encodeToString(preKeySignalmessage.serialize(), Base64.DEFAULT)}")
        return Base64.encodeToString(preKeySignalmessage.serialize(), Base64.DEFAULT)
    }

    fun decrypt(message: String): String {
        Log.d("SMD d", "message: $message")
        var message : ByteArray = Base64.decode(message, Base64.DEFAULT)
        Log.d("SMD d", "message: ${message.toUByteArray()}")
        getKeysDetails()
        Log.d("SMD d", "called")
        session()
        Log.d("SMD d", "Called")
        var decryptedMessage : ByteArray = sessionCipher.decrypt(PreKeySignalMessage(message))
        Log.d("SMD d", "DM ${String(decryptedMessage, StandardCharsets.UTF_8)}")
        return String(decryptedMessage, StandardCharsets.UTF_8)
    }

    private fun resetStatus() {
        status = 0
        message = ""
    }
}