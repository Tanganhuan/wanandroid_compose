package com.go.wanandroid.data.persistent

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.go.common.datastore.AbstractPreferencesDataStore
import kotlinx.coroutines.flow.Flow

object AppPreferencesDataStore: AbstractPreferencesDataStore() {
    private const val TAG = "AppDataStoreTAG"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

    override fun createDataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    private val IS_FIRST_TIME_TO_USE_APP = booleanPreferencesKey("IS_FIRST_TIME_TO_USE_APP")

    fun isFirstTimeToUseApp(): Flow<Boolean> {
        return getValueFlow(IS_FIRST_TIME_TO_USE_APP,true)
    }

    suspend fun setIsFirstTimeToUseApp(isFirst: Boolean) {
        setValue(IS_FIRST_TIME_TO_USE_APP,isFirst)
    }

}
