package com.psw9999.gongmozootopia.Repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException

class ConfigurationRepository(private val context: Context) {
    private val IS_FORFEITED_ENABLED = booleanPreferencesKey("forfeitedStock_Enabled")
    private val IS_SPAC_ENABLED = booleanPreferencesKey("spacStock_Enabled")
    private val stockFirmList =
        listOf("DB","IBK", "KB", "NH", "SK", "대신", "메리츠", "미래에셋", "삼성", "상상인", "신영", "신한", "유안타", "유진", "키움",
            "하나", "하이", "한국", "한화", "현대차")
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
        }.flowOn(Dispatchers.IO)

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
        }.flowOn(Dispatchers.IO)

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
        }.flowOn(Dispatchers.IO)

    suspend fun setSpacEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SPAC_ENABLED] = isEnabled
        }
    }
}


