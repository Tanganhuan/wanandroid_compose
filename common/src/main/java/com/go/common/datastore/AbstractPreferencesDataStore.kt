package com.go.common.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.go.common.BaseApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class AbstractPreferencesDataStore {

    abstract fun createDataStore(context:Context):DataStore<Preferences>

    protected val dataStore by lazy {
        createDataStore(BaseApplication.Instance)
    }

    protected fun <T> getValueFlow(key:Preferences.Key<T>,defalutValue:T): Flow<T> =
        dataStore.data.map { preferences ->
            preferences[key] ?: defalutValue
        }

    protected suspend fun <T> setValue(key:Preferences.Key<T>,updateValue:T) {
        dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[key] = updateValue
            }
        }
    }
}