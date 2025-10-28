package com.example.expenses.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

object PreferenceKeys {
    val CURRENCY = stringPreferencesKey("currency")
    val PIN_HASH = stringPreferencesKey("pin_hash")
    val PIN_REQUIRED = booleanPreferencesKey("pin_required")
}

class UserPreferences(private val context: Context) {

    val currencyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.CURRENCY] ?: "KWD"
    }

    val pinRequiredFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.PIN_REQUIRED] ?: false
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.CURRENCY] = currency
        }
    }

    suspend fun setPin(pin: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.PIN_HASH] = hashPin(pin)
            prefs[PreferenceKeys.PIN_REQUIRED] = true
        }
    }

    suspend fun validatePin(pin: String): Boolean {
        val hash = hashPin(pin)
        val stored = context.dataStore.data.map { it[PreferenceKeys.PIN_HASH] }.map { it == hash }
        return stored.firstOrNull() == true
    }

    suspend fun isPinRequired(): Boolean =
        context.dataStore.data.map { it[PreferenceKeys.PIN_REQUIRED] ?: false }.firstOrNull() ?: false

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(pin.toByteArray())
        return hash.joinToString(separator = "") { "%02x".format(it) }
    }
}
