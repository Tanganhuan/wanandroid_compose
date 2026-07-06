package com.go.mine.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.blankj.utilcode.util.LogUtils
import com.go.common.http.HttpConfig
import com.go.common.navigation3.EmptyNavigator
import com.go.common.navigation3.INavigator
import com.go.common.navigation3.WebViewNavKey
import com.go.mine.R
import com.go.mine.data.LoginUiState
import com.go.mine.data.UserBean
import com.go.mine.nav.LoginScreenNavKey
import com.go.mine.nav.SettingsScreenNavKey
import com.go.mine.viewmodel.UserViewModel
import com.go.common.widget.ColumnItemsBox
import com.go.common.widget.ItemBoxData
import com.go.mine.data.emptyUserBean
import com.go.mine.nav.CollectScreenNavKey

@Preview
@Composable
fun MineScreenPreview() {
    MineScreen(EmptyNavigator(), loginUiState = LoginUiState.LoggedIn, emptyUserBean)
}

private const val TAG = "MineScreenTAG"

@Composable
fun MineScreen(navigator: INavigator) {
    LogUtils.d("LocalViewModelStoreOwnerTAG","MineScreen:${LocalViewModelStoreOwner.current}")
    val userViewModel = viewModel<UserViewModel>(
        viewModelStoreOwner = LocalActivity.current as ViewModelStoreOwner,
        factory = UserViewModel.Factory
    )
    val uiState = userViewModel.loginUiState
    val userBean = userViewModel.userBean

    LaunchedEffect(Unit) {
        userViewModel.getUserCoinInfo()
    }

    MineScreen(
        navigator = navigator,
        loginUiState = uiState,
        userBean = userBean)
}

@Composable
fun MineScreen(
    navigator: INavigator,
    loginUiState: LoginUiState,
    userBean: UserBean,
) {

    val blockModifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {

        Box(modifier = Modifier
            .height(130.dp)
            .then(blockModifier)) {
            LoginComposable(
                navigator = navigator,
                loginUiState = loginUiState,
                userBean = userBean
            )
        }

        val mineItems = listOf<ItemBoxData>(
            ItemBoxData.ClickableItemData(title = stringResource(R.string.my_favorite)) {
                navigator.navigate(CollectScreenNavKey)
            },
        )
        ColumnItemsBox(mineItems)

        val settingsItems = listOf<ItemBoxData>(
            ItemBoxData.ClickableItemData(title = stringResource(R.string.setting)) {
                navigator.navigate(SettingsScreenNavKey)
            }
        )
        ColumnItemsBox(settingsItems)
    }
}

@Composable
private fun BoxScope.LoginComposable(
    navigator: INavigator,
    loginUiState: LoginUiState,
    userBean: UserBean,
) {
    if (loginUiState !is LoginUiState.LoggedIn) {
        NoLoginComposable(navigator)
    } else {
        val userBean =
        Row(modifier = Modifier
            .padding(10.dp)
            .align(Alignment.CenterStart)) {
            val painterResource = painterResource(R.drawable.wanandroid_logo)
            AsyncImage(
                model = userBean.icon,
                placeholder = painterResource,
                error = painterResource,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Inside
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(userBean.nickname, fontWeight = FontWeight.Bold)
                Text("ID:${userBean.id} \t积分：${userBean.coinCount}")
            }
        }
    }

}

@Composable
private fun BoxScope.NoLoginComposable(navigator: INavigator) {
    Button(onClick = {
        navigator.navigate(LoginScreenNavKey())
    }, modifier = Modifier.align(Alignment.Center)) {
        Text(stringResource(R.string.login_or_register))
    }
}
