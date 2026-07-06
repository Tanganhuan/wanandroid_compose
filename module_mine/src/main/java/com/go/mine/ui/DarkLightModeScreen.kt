package com.go.mine.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.go.common.datastore.AppThemeStateDataStore
import com.go.common.navigation3.EmptyNavigator
import com.go.common.navigation3.INavigator
import com.go.common.theme.emptyAppThemeState
import com.go.common.widget.AppTopBackBox
import com.go.mine.R
import com.go.common.widget.ColumnItemsBox
import com.go.common.widget.ItemBoxData
import kotlinx.coroutines.launch

private const val TAG = "DarkLightModeScreenTAG"

@Preview
@Composable
fun DarkLightModeScreenPreview() {
    DarkLightModeScreen(EmptyNavigator())
}

@Composable
fun DarkLightModeScreen(navigator: INavigator) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val rememberCoroutineScope = rememberCoroutineScope()
    val appThemeState = AppThemeStateDataStore.read()
        .collectAsStateWithLifecycle(initialValue = emptyAppThemeState).value

    Column {
        AppTopBackBox(title = stringResource(R.string.dark_mode), onBackClick = {
            navigator.goBack()
        })

        val followSystemSettings = listOf<ItemBoxData>(
            ItemBoxData.SwitchItemData(
                title = stringResource(R.string.follow_the_system_settings),
                desc = stringResource(R.string.follow_the_system_settings_desc),
                checked = appThemeState.darkThemeFollowSystem,
            ) {
                rememberCoroutineScope.launch {
                    AppThemeStateDataStore.write(appThemeState.copy(darkThemeFollowSystem = it))
                }
            }
        )

        ColumnItemsBox(followSystemSettings)
        if (appThemeState.darkThemeFollowSystem) {
            return@Column
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            stringResource(R.string.choose_manually),
            modifier = Modifier.padding(start = 10.dp)
        )

        val darkLightItems = listOf<ItemBoxData>(
            ItemBoxData.RadioItemData(
                title = stringResource(R.string.light_mode),
                checked = !appThemeState.darkTheme
            ) {
                rememberCoroutineScope.launch {
                    AppThemeStateDataStore.write(appThemeState.copy(darkTheme = false))
                }
            },

            ItemBoxData.RadioItemData(
                title = stringResource(R.string.dark_mode),
                checked = appThemeState.darkTheme
            ) {
                rememberCoroutineScope.launch {
                    AppThemeStateDataStore.write(appThemeState.copy(darkTheme = true))
                }
            }
        )
        ColumnItemsBox(darkLightItems)
    }

}