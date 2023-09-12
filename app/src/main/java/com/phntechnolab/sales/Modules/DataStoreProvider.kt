package com.phntechnolab.sales.Modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreProvider @Inject constructor() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private var instance: DataStore<Preferences>? = null
    fun getDataStoreInstance(context: Context): DataStore<Preferences> {
        instance = context.dataStore
        return instance as DataStore<Preferences>
    }
}