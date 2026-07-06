package com.go.mine.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.blankj.utilcode.util.LogUtils
import com.go.common.navigation3.INavigator
import com.go.common.widget.AppTopBackBox
import com.go.mine.R
import com.go.mine.data.LoginUiState
import com.go.mine.nav.LoginScreenNavKey
import com.go.mine.nav.RegisterScreenNavKey
import com.go.mine.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "LoginScreenTAG"


@Composable
fun LoginScreen(navigator: INavigator,key: LoginScreenNavKey) {
    LogUtils.d("LocalViewModelStoreOwnerTAG","LoginScreen:${LocalViewModelStoreOwner.current}")
    val userViewModel = viewModel<UserViewModel>(
        viewModelStoreOwner = LocalActivity.current as ViewModelStoreOwner,
        factory = UserViewModel.Factory
    )


    val rememberCoroutineScope = rememberCoroutineScope()
    SideEffect {
        LogUtils.d(TAG,"LoginScreen UserViewModel:${userViewModel.hashCode()}")
    }
    val loginUiState = userViewModel.loginUiState
    LoginScreen(
        navigator = navigator,
        loginUiState = loginUiState,
        login = { userName, password ->
        userViewModel.login(userName, password)
    }, onLogined = {
        rememberCoroutineScope.launch {
            delay(1000)
            navigator.goBack()
            if(key.redirectToKey != null) {
                navigator.navigate(key.redirectToKey as NavKey)
            }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigator: INavigator,
    loginUiState: LoginUiState,
    login: (String, String) -> Unit,
    onLogined:()->Unit,
) {

    val rememberAccountNumberTextFieldState = rememberTextFieldState()
    val rememberPasswordTextFieldState = rememberTextFieldState()

    Column(modifier = Modifier.fillMaxWidth()) {
        AppTopBackBox(title = stringResource(R.string.login), trailingContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable { navigator.navigate(RegisterScreenNavKey) }) {
                Text(
                    stringResource(R.string.register),
                    modifier = Modifier
                        .padding(end = 10.dp, start = 10.dp)
                        .align(Alignment.Center),
                )
            }
        }, onBackClick = {
            navigator.goBack()
        })

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            state = rememberAccountNumberTextFieldState,
            label = {
                Text(stringResource(R.string.account_number))
            },
            placeholder = {
                Text(stringResource(R.string.please_enter_your_login_account))
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            state = rememberPasswordTextFieldState,
            label = {
                Text(stringResource(R.string.password))
            },
            placeholder = {
                Text(stringResource(R.string.please_enter_your_login_password))
            }
        )

        Spacer(modifier = Modifier.height(50.dp))

        LoginUiStateButton(
            loginUiState = loginUiState,
            buttonText = stringResource(R.string.login),
            onLogined = onLogined,
            onClick = {
                login(
                    rememberAccountNumberTextFieldState.text.toString(),
                    rememberPasswordTextFieldState.text.toString()
                )
            }
        )
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LoginUiStateButton(
    loginUiState: LoginUiState,
    buttonText: String,
    onClick: () -> Unit,
    onLogined:()->Unit,
) {
    val rememberTooltipState = rememberTooltipState()

    LaunchedEffect(loginUiState) {
        if (loginUiState is LoginUiState.Idle) {
            if (rememberTooltipState.isVisible) {
                rememberTooltipState.dismiss()
            }
        } else {
            if (loginUiState is LoginUiState.LoggedIn || loginUiState is LoginUiState.Error) {
                launch() {
                    rememberTooltipState.show()
                }
            }
            if (loginUiState is LoginUiState.LoggedIn) {
                onLogined()
            }
        }
    }

    TooltipBox(
        modifier = Modifier,
        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip {
                val msg = when (loginUiState) {
                    is LoginUiState.Logging -> stringResource(R.string.logging)
                    is LoginUiState.Error -> loginUiState.errorMessage
                    is LoginUiState.LoggedIn -> stringResource(R.string.login_successful)
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
                if (loginUiState is LoginUiState.Logging) {
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
