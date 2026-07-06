package com.go.common.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.go.common.BaseApplication
import kotlinx.coroutines.flow.Flow

abstract class AbstractJsonDataStore<T>(val name: String,val serializer: Serializer<T>) {

    val dataStore by lazy {
        createDataStore(BaseApplication.Instance)
    }

    private val Context.dataStore: DataStore<T> by dataStore(
        fileName = name,
        serializer = serializer,
    )

    protected fun createDataStore(context: Context): DataStore<T> {
        return context.dataStore
    }

    fun read(): Flow<T> {
        return dataStore.data
    }

    suspend fun write(transform: suspend (t: T) -> T) {
        dataStore.updateData(transform)
    }

    suspend fun write(bean:T) {
        dataStore.updateData { old ->
            bean
        }
    }

}