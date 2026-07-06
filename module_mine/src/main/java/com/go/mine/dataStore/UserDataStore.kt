package com.go.mine.dataStore

import com.go.common.datastore.AbstractJsonDataStore
import com.go.common.datastore.AbstractJsonDataStoreSerializer
import com.go.mine.data.UserBean
import com.go.mine.data.emptyUserBean

class UserDataStore: AbstractJsonDataStore<UserBean>(
    name = "user.json",
    serializer = AbstractJsonDataStoreSerializer.create(
        default = emptyUserBean,
        serializer = UserBean.serializer(),
    )
) {
    companion object {
        val Instance by lazy {
            UserDataStore()
        }
    }

}
