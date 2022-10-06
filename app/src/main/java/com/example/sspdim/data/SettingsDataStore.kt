package com.example.sspdim.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "user"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
)

class SettingsDataStore(context: Context) {
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val USERNAME = stringPreferencesKey("username")
    private val SERVER = stringPreferencesKey("server")
    private val FCM_TOKEN_SENT = booleanPreferencesKey("fcm_token_sent")

    val isLoggedInPreferenceFlow: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            }
            else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val usernamePreference: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            }
            else {
                throw it
            }
        }
        .map { preferences ->
            preferences[USERNAME] ?: ""
        }

    val serverPreference: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            }
            else {
                throw it
            }
        }
        .map { preferences ->
            preferences[SERVER] ?: ""
        }

    val fcmTokenSentPreference: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            }
            else {
                throw it
            }
        }
        .map { preferences ->
            preferences[FCM_TOKEN_SENT] ?: false
        }

    suspend fun saveLoggedInPreference(isLoggedIn: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun saveUsernamePreference(username: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }

    suspend fun saveServerPreference(server: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[SERVER] = server
        }
    }

    suspend fun saveFcmTokenSentPreference(fcmTokenSent: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[FCM_TOKEN_SENT] = fcmTokenSent
        }
    }
}