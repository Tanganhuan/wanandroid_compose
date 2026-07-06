package com.go.navigate.dataStore

import com.go.common.datastore.AbstractJsonDataStore
import com.go.common.datastore.AbstractJsonDataStoreSerializer
import com.go.navigate.data.AndroidNavigateTreeBean
import com.go.navigate.data.KnowledgeList
import com.go.navigate.data.SearchHotkeyList
import com.go.navigate.data.emptySearchHotkeyList


class AndroidNavigateTreeStore: AbstractJsonDataStore<AndroidNavigateTreeBean>(
    name = "android_navigate_tree.json",
    serializer = AbstractJsonDataStoreSerializer.create(
        default = AndroidNavigateTreeBean(),
        serializer = AndroidNavigateTreeBean.serializer(),
    )
) {
    companion object {
        val Instance by lazy {
            AndroidNavigateTreeStore()
        }
    }
}


class SearchHotkeyListStore: AbstractJsonDataStore<SearchHotkeyList>(
    name = "search_hotkey.json",
    serializer = AbstractJsonDataStoreSerializer.create(
        default = emptySearchHotkeyList,
        serializer = SearchHotkeyList.serializer(),
    )
) {
    companion object {
        val Instance by lazy {
            SearchHotkeyListStore()
        }
    }
}


class SearchInputStore: AbstractJsonDataStore<SearchHotkeyList>(
    name = "search_input_key.json",
    serializer = AbstractJsonDataStoreSerializer.create(
        default = emptySearchHotkeyList,
        serializer = SearchHotkeyList.serializer(),
    )
) {
    companion object {
        val Instance by lazy {
            SearchInputStore()
        }
    }
}


class KnowledgeListStore: AbstractJsonDataStore<KnowledgeList>(
    name = "knowledge_system_list.json",
    serializer = AbstractJsonDataStoreSerializer.create(
        default = KnowledgeList(),
        serializer = KnowledgeList.serializer(),
    )
) {
    companion object {
        val Instance by lazy {
            KnowledgeListStore()
        }
    }
}