package com.go.common.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ImmutableListJsonAdapter<T>(
    private val elementAdapter: JsonAdapter<T>
) : JsonAdapter<ImmutableList<T>>() {

    @FromJson
    override fun fromJson(reader: JsonReader): ImmutableList<T> {
        val list = mutableListOf<T>()
        reader.beginArray()
        while (reader.hasNext()) {
            elementAdapter.fromJson(reader)?.let {
                list.add(it)
            }
        }
        reader.endArray()
        return list.toImmutableList()
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: ImmutableList<T>?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginArray()
        for (element in value) {
            elementAdapter.toJson(writer, element)
        }
        writer.endArray()
    }

    object Factory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: MutableSet<out Annotation>,
            moshi: Moshi
        ): JsonAdapter<*>? {
            if (annotations.isNotEmpty()) return null
            if (type !is ParameterizedType) return null

            // 检查是否为 ImmutableList
            if (type.rawType == ImmutableList::class.java) {
                val elementType = type.actualTypeArguments[0]
                val elementAdapter = moshi.adapter<Any>(elementType)
                return ImmutableListJsonAdapter(elementAdapter)
            }
            return null
        }
    }
}