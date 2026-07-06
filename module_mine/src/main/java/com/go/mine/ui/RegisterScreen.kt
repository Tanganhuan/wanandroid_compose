package com.go.mine.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.go.common.navigation3.EmptyNavigator
import com.go.common.navigation3.INavigator
import com.go.common.widget.AppTopBackBox
import com.go.common.widget.SingleButtonDialog
import com.go.mine.R
import com.go.mine.data.RegisterUiState
import com.go.mine.viewmodel.RegisterViewModel

@Preview(apiLevel = 36)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(EmptyNavigator(), RegisterUiState.InProgress) { s1, s2, s3 ->

    }
}

@Composable
fun RegisterScreen(navigator: INavigator) {
    val registerViewModel = viewModel<RegisterViewModel>(
        factory = RegisterViewModel.Factory
    )
    val registerUiState = registerViewModel.uiState.collectAsStateWithLifecycle().value
    RegisterScreen(navigator = navigator, registerUiState) { account, password, repassword ->
        registerViewModel.register(account, password, repassword)
    }
}

@Composable
fun RegisterScreen(
    navigator: INavigator,
    registerUiState: RegisterUiState,
    register: (String, String, String) -> Unit,
) {
    val accountState = rememberTextFieldState()
    val passwordState = rememberTextFieldState()
    val repasswordState = rememberTextFieldState()

    if (registerUiState is RegisterUiState.Succeed) {
        SingleButtonDialog(
            contentText = stringResource(R.string.registered_successfully),
            buttonText = stringResource(R.string.go_login)
        ) {
            navigator.goBack()
        }
    } else if (registerUiState is RegisterUiState.Error) {
        SingleButtonDialog(
            contentText = registerUiState.errorMessage,
            buttonText = stringResource(com.go.common.R.string.i_know)
        ) {}
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AppTopBackBox(title = stringResource(R.string.register), onBackClick = {
            navigator.goBack()
        })

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            state = accountState,
            label = {
                Text(stringResource(R.string.account_number))
            },
            placeholder = {
                Text(stringResource(R.string.please_enter_your_register_account))
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            state = passwordState,
            label = {
                Text(stringResource(R.string.password))
            },
            placeholder = {
                Text(stringResource(R.string.please_enter_your_register_password))
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            state = repasswordState,
            label = {
                Text(stringResource(R.string.repassword))
            },
            placeholder = {
                Text(stringResource(R.string.please_enter_your_register_password))
            }
        )

        Spacer(modifier = Modifier.height(50.dp))
        Button(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            onClick = {
                register(
                    accountState.text.toString(),
                    passwordState.text.toString(),
                    repasswordState.text.toString()
                )
            },
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (registerUiState is RegisterUiState.InProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.register))
            }
        }
    }
}
