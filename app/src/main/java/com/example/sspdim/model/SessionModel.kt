package com.example.sspdim.model

import android.content.Context
import android.util.Base64
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
    private var numberOfPrekeys: Int = 0

    private var registrationId : Int = 0
    private lateinit var identityKeyPair : ByteArray
    private lateinit var signedPreKey : ByteArray
    private lateinit var preKey : ByteArray
    private lateinit var preKeys : List<ByteArray>

    private lateinit var store : InMemorySignalProtocolStore
    private var address : SignalProtocolAddress = SignalProtocolAddress(username, 1)
    private lateinit var sessionBuilder : SessionBuilder
    private lateinit var sessionCipher : SessionCipher

    private fun session() {
        var preKey = PreKeyRecord(preKey)
        var signedPreKey = SignedPreKeyRecord(signedPreKey)
        var identityKeyPair = IdentityKeyPair(identityKeyPair)
        store = InMemorySignalProtocolStore(identityKeyPair, 9017)
        store.storeSignedPreKey(signedPreKey.id, signedPreKey)
        preKeys.forEach { key -> store.storePreKey(PreKeyRecord(key).id, PreKeyRecord(key)) }
        sessionBuilder = SessionBuilder(store, address)
        var preKeyBundle = PreKeyBundle(registrationId, address.deviceId, preKey.id, preKey.keyPair.publicKey, signedPreKey.id, signedPreKey.keyPair.publicKey, signedPreKey.signature, identityKeyPair.publicKey)
        sessionBuilder.process(preKeyBundle)
        sessionCipher = SessionCipher(store, address)
    }

    private fun getKeysDetails() {
        resetStatus()
        val request = GetKeys(username)
        runBlocking {
            launch {
                try {
                    response = SspdimApi.retrofitService.getKeys(request)
                    status = response!!.status
                    registrationId = response!!.registrationid
                    identityKeyPair = response!!.identitykeypair
                    signedPreKey = response!!.signedprekey
                    preKey = response!!.prekey
                    numberOfPrekeys = response!!.numberOfPrekeys
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun encrypt(context: Context, message: ByteArray): String? {
        getKeysDetails()
        val keysModel = KeysModel(context, username)
        preKeys = keysModel.getPrekeys()
        session()
        var encryptedMessage : CiphertextMessage = sessionCipher.encrypt(message)
        var preKeySignalmessage : PreKeySignalMessage = PreKeySignalMessage(encryptedMessage.serialize())
        return Base64.encodeToString(preKeySignalmessage.serialize(), Base64.DEFAULT)
    }

    fun decrypt(context: Context, message: String): String {
        var message : ByteArray = Base64.decode(message, Base64.DEFAULT)
        val keysModel = KeysModel(context, username)
        getKeysDetails()
        if (numberOfPrekeys < 5) {
            keysModel.addPrekeys()
        }
        preKeys = keysModel.getPrekeys()
        session()
        var decryptedMessage : ByteArray = sessionCipher.decrypt(PreKeySignalMessage(message))
        return String(decryptedMessage, StandardCharsets.UTF_8)
    }

    fun decryptNotification(context: Context, message: String): String {
        var message : ByteArray = Base64.decode(message, Base64.DEFAULT)
        val keysModel = KeysModel(context, username)
        getKeysDetails()
        preKeys = keysModel.getPrekeys()
        session()
        var decryptedMessage : ByteArray = sessionCipher.decrypt(PreKeySignalMessage(message))
        return String(decryptedMessage, StandardCharsets.UTF_8)
    }

    private fun resetStatus() {
        status = 0
        message = ""
    }
}