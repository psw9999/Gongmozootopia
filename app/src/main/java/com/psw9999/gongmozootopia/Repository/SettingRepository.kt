package com.psw9999.gongmozootopia.Repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

class SettingRepository(var context: Context) {
    private val IS_FORFEITED_ENABLED = booleanPreferencesKey("forfeitedStock_Enabled")
    private val IS_SPAC_ENABLED = booleanPreferencesKey("spacStock_Enabled")
    private val stockFirmList = listOf("미래", "한국", "키움", "NH", "삼성", "메리츠", "하나", "KB", "대신", "신영", "신한", "IBK", "DB", "한화")
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private fun isStockFirmRegistered(stockFirmName : String) : Preferences.Key<Boolean>
        = booleanPreferencesKey(stockFirmName)

    val stockFirmFlow : Flow<Map<String,Boolean>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            var stockFirmMap = mutableMapOf<String,Boolean>()
            stockFirmList.forEach { firmName ->
                var isFirmEnabled = preferences[isStockFirmRegistered(firmName)]?: false
                stockFirmMap[firmName] = isFirmEnabled
            }
            stockFirmMap
        }

    suspend fun setStockFirmEnable(firmName : String, isEnabled : Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isStockFirmRegistered(firmName)] = isEnabled
        }
    }

    val forfeitedStockFlow : Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences->
            var isForfeitedEnabled =  preferences[IS_FORFEITED_ENABLED]?: false
            isForfeitedEnabled
        }

    suspend fun setForfeitedEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_FORFEITED_ENABLED] = isEnabled
        }
    }

    val spacStockFlow : Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences->
            var isSpacEnabled = preferences[IS_SPAC_ENABLED]?: false
            isSpacEnabled
        }

    suspend fun setSpacEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SPAC_ENABLED] = isEnabled
        }
    }
}


