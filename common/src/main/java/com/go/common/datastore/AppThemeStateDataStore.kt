package com.go.common.datastore

import com.go.common.theme.AppThemeState
import com.go.common.theme.emptyAppThemeState

object AppThemeStateDataStore : AbstractJsonDataStore<AppThemeState>(
    name = "app_theme.json",
    serializer = AbstractJsonDataStoreSerializer.create(
        default = emptyAppThemeState,
        serializer = AppThemeState.serializer(),
    )
)