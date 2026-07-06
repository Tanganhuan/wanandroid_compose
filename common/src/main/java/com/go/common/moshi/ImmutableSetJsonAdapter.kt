package com.go.common.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet


// 2. ImmutableSet 的适配器
class ImmutableSetJsonAdapter<T>(private val elementAdapter: JsonAdapter<T>) : JsonAdapter<ImmutableSet<T>>() {
    @FromJson
    override fun fromJson(reader: JsonReader): ImmutableSet<T> {
        val set = mutableSetOf<T>()
        reader.beginArray()
        while (reader.hasNext()) {
            set.add(elementAdapter.fromJson(reader)!!)
        }
        reader.endArray()
        return set.toImmutableSet()
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: ImmutableSet<T>?) {
        if (value == null) { writer.nullValue(); return }
        writer.beginArray()
        for (element in value) elementAdapter.toJson(writer, element)
        writer.endArray()
    }
}