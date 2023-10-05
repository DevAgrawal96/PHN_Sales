package com.phntechnolab.sales.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.model.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object DataStoreManager {

    suspend fun setToken(
        context: Context,
        dataStoreProvider: DataStoreProvider,
        key: String,
        value: String
    ) {
        val dataStore = dataStoreProvider.getDataStoreInstance(context)
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { role ->
            role[dataStoreKey] = value
        }
    }

    suspend fun getToken(
        context: Context,
        dataStoreProvider: DataStoreProvider,
        key: String
    ): String? {
        val dataStore = dataStoreProvider.getDataStoreInstance(context)
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences?.get(dataStoreKey)
    }

    suspend fun setUser(
        context: Context,
        dataStoreProvider: DataStoreProvider,
        key: String,
        value: UserResponse
    ) {
        val dataStore = dataStoreProvider.getDataStoreInstance(context)
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { role ->
            role[dataStoreKey] = Gson().toJson(value)
        }
    }

    suspend fun clearDataStore(
        context: Context,
        dataStoreProvider: DataStoreProvider
    ) {
        val dataStore = dataStoreProvider.getDataStoreInstance(context)
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun getUser(
        context: Context,
        dataStoreProvider: DataStoreProvider,
        key: String
    ): UserResponse? {
        val dataStore = dataStoreProvider.getDataStoreInstance(context)
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return Gson().fromJson(preferences[dataStoreKey], UserResponse::class.java)
    }

}