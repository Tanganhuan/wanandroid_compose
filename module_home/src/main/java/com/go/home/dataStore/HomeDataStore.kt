package com.go.home.dataStore

import com.go.common.datastore.AbstractJsonDataStore
import com.go.common.datastore.AbstractJsonDataStoreSerializer
import com.go.home.data.HomeHeadData
import com.go.home.data.emptyHomeHeadData


class HomeDataStore: AbstractJsonDataStore<HomeHeadData>(
    name = "home_data_store.json",
    serializer = AbstractJsonDataStoreSerializer.create(
        default = emptyHomeHeadData,
        serializer = HomeHeadData.serializer(),
    )
) {
    companion object {
        val Instance by lazy {
            HomeDataStore()
        }
    }
}