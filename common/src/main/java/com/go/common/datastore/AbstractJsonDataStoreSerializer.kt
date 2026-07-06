package com.go.common.datastore

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.json.Json

class AbstractJsonDataStoreSerializer<T>(val default: T,
                                         val serializer: KSerializer<T>): Serializer<T> {

    override suspend fun readFrom(input: InputStream): T {
        return Json.decodeFromString(
            deserializer = serializer,
            input.readBytes().decodeToString()
        )
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(serializer = serializer, t).encodeToByteArray()
            )
        }
    }

    override val defaultValue: T
        get() = default

    companion object {
        fun <T> create(default: T,
                       serializer: KSerializer<T>): Serializer<T>  {
            return AbstractJsonDataStoreSerializer(default,serializer)
        }
    }
}