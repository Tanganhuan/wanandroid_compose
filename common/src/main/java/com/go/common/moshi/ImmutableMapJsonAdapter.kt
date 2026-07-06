package com.go.common.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap

// 3. ImmutableMap 的适配器
class ImmutableMapJsonAdapter<K, V>(
    private val keyAdapter: JsonAdapter<K>,
    private val valueAdapter: JsonAdapter<V>
) : JsonAdapter<ImmutableMap<K, V>>() {
    @FromJson
    override fun fromJson(reader: JsonReader): ImmutableMap<K, V> {
        val map = mutableMapOf<K, V>()
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            // 注意：Map的Key在JSON中永远是String，这里需要你的keyAdapter能处理String
            val key = keyAdapter.fromJsonValue(name)!!
            val value = valueAdapter.fromJson(reader)!!
            map[key] = value
        }
        reader.endObject()
        return map.toImmutableMap()
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: ImmutableMap<K, V>?) {
        if (value == null) { writer.nullValue(); return }
        writer.beginObject()
        for ((k, v) in value) {
            writer.name(keyAdapter.toJsonValue(k) as String)
            valueAdapter.toJson(writer, v)
        }
        writer.endObject()
    }
}