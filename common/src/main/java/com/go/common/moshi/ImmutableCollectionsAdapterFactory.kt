package com.go.common.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


// 4. 统一的工厂类（直接复制这段去注册即可）
object ImmutableCollectionsAdapterFactory : JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (annotations.isNotEmpty()) return null
        if (type !is ParameterizedType) return null

        val rawType = type.rawType
        val typeArgs = type.actualTypeArguments

        return when (rawType) {
            ImmutableList::class.java -> {
                val elementType = typeArgs[0]
                val elementAdapter = moshi.adapter<Any>(elementType)
                ImmutableListJsonAdapter(elementAdapter)
            }
            ImmutableSet::class.java -> {
                val elementType = typeArgs[0]
                val elementAdapter = moshi.adapter<Any>(elementType)
                ImmutableSetJsonAdapter(elementAdapter)
            }
            ImmutableMap::class.java -> {
                val keyType = typeArgs[0]
                val valueType = typeArgs[1]
                val keyAdapter = moshi.adapter<Any>(keyType)
                val valueAdapter = moshi.adapter<Any>(valueType)
                ImmutableMapJsonAdapter(keyAdapter, valueAdapter)
            }
            else -> null
        }
    }
}