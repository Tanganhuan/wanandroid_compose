package com.go.mine.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.LogUtils
import com.go.common.datastore.AppThemeStateDataStore
import com.go.common.extension.hex
import com.go.common.extension.toColor
import com.go.common.navigation3.EmptyNavigator
import com.go.common.navigation3.INavigator
import com.go.common.theme.emptyAppThemeState
import com.go.common.widget.AppTopBackBox
import com.go.mine.R
import com.go.mine.data.LoginUiState
import com.go.mine.nav.DarkLightModeScreenNavKey
import com.go.mine.nav.ThemeSettingScreenNavKey
import com.go.mine.viewmodel.UserViewModel
import com.go.common.widget.ColumnItemsBox
import com.go.common.widget.ItemBoxData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navigator = EmptyNavigator(), LoginUiState.LoggedIn, logout = {})
}

private const val TAG = "SettingsScreenTAG"
@Composable
fun SettingsScreen(navigator: INavigator) {
    val userViewModel = viewModel<UserViewModel>(
        viewModelStoreOwner = LocalActivity.current as ViewModelStoreOwner,
        factory = UserViewModel.Factory
    )
    SideEffect {
        LogUtils.d(TAG,"SettingsScreen UserViewModel:${userViewModel.hashCode()}")
    }

    val uiState = userViewModel.loginUiState

    LaunchedEffect(Unit) {
        if (uiState is LoginUiState.LogoutSucceed) {
            navigator.goBack()
        }
    }

    SettingsScreen(navigator, uiState, logout = {
        userViewModel.logout()
    })
}

@Composable
fun SettingsScreen(navigator: INavigator, loginUiState: LoginUiState, logout: () -> Unit) {
    val appThemeState = AppThemeStateDataStore.read()
        .collectAsStateWithLifecycle(initialValue = emptyAppThemeState).value
    val primaryColor = appThemeState.primaryColor
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AppTopBackBox(
            title = stringResource(R.string.setting),
            onBackClick = {
                navigator.goBack()
            }
        )

        val result = if(appThemeState.darkThemeFollowSystem) {
            stringResource(R.string.follow_the_system_settings)
        } else if(appThemeState.darkTheme) {
            stringResource(R.string.dark_mode)
        } else {
            stringResource(R.string.light_mode)
        }

        val themeItems = listOf<ItemBoxData>(
            ItemBoxData.ClickableItemData(
                title = stringResource(R.string.dark_mode),
                result = result
            ) {
                navigator.navigate(DarkLightModeScreenNavKey)
            },
            ItemBoxData.ClickableItemData(title = stringResource(R.string.theme_color),
                result = primaryColor.toColor().hex) {
                navigator.navigate(ThemeSettingScreenNavKey)
            }
        )
        ColumnItemsBox(themeItems)

        Spacer(modifier = Modifier.height(20.dp))
        if (loginUiState is LoginUiState.LoggedIn
            || loginUiState is LoginUiState.Logouting
            || loginUiState is LoginUiState.LogoutSucceed
        ) {
            LoginUiStateButton(
                buttonText = stringResource(R.string.go_logout),
                navigator = navigator, loginUiState = loginUiState
            ) {
                logout()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LoginUiStateButton(
    loginUiState: LoginUiState,
    navigator: INavigator,
    buttonText: String,
    onClick: () -> Unit,
) {
    val rememberTooltipState = rememberTooltipState()
    LaunchedEffect(loginUiState) {

        if (loginUiState is LoginUiState.Idle) {
            if (rememberTooltipState.isVisible) {
                rememberTooltipState.dismiss()
            }
        } else {
            if (loginUiState is LoginUiState.LogoutSucceed
                || loginUiState is LoginUiState.Error
            ) {
                launch {
                    rememberTooltipState.show()
                }
            }
            if (loginUiState is LoginUiState.LogoutSucceed) {
                launch {
                    delay(1000)
                    navigator.goBack()
                }
            }
        }
    }

    TooltipBox(
        modifier = Modifier,
        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip {
                val msg = when (loginUiState) {
//                    is LoginUiState.Logouting -> stringResource(R.string.logging_out_in_progress)
                    is LoginUiState.Error -> loginUiState.errorMessage
                    is LoginUiState.LogoutSucceed -> stringResource(R.string.logout_successful)
                    else -> ""
                }
                Text(msg)
            }
        },
        state = rememberTooltipState
    ) {
        Button(
            onClick = {
                onClick()
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
        ) {
            Row {
                if (loginUiState is LoginUiState.Logouting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(buttonText)
            }
        }
    }
}
