package com.go.common.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.go.common.R
import com.go.common.theme.LocalAppThemeState

@Preview(apiLevel = 36)
@Composable
fun AppTopBackBoxPreview() {
    AppTopBackBox(title = "登陆", onBackClick = {}, trailingContent = {
        Icon(painter = painterResource(R.drawable.favorite),
            contentDescription = null,
            modifier = Modifier.fillMaxHeight().clickable { })
    })
}

@Composable
fun AppTopBox(
    title: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable BoxScope.() -> Unit = { },
    onBackClick: (() -> Unit)? = null,
) {
    AppTopBackBox(
        modifier = modifier,
        title = title,
        showBackIcon = false,
        trailingContent = trailingContent,
        onBackClick = onBackClick
    )
}

@Composable
fun AppTopBackBox(
    modifier: Modifier = Modifier,
    title: String,
    showBackIcon: Boolean = true,
    trailingContent: (@Composable BoxScope.() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
) {
    val appThemeState = LocalAppThemeState.current
    val isTitleBarFollowsThemeColor = if ((appThemeState.darkThemeFollowSystem && isSystemInDarkTheme()) || appThemeState.darkTheme) false
    else appThemeState.statusBarFollowPrimaryColor

    val boxModifier =
        if (isTitleBarFollowsThemeColor)
            modifier.then(Modifier.background(MaterialTheme.colorScheme.primary))
        else modifier

    Box(
        modifier = boxModifier.then(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(50.dp)
        ),
        contentAlignment = Alignment.CenterStart
    ) {

        if (showBackIcon) {
            Icon(
                painter = painterResource(R.drawable.keyboard_backspace),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier
                    .clickable { onBackClick?.invoke() }
                    .align(Alignment.CenterStart)
                    .height(50.dp)
                    .padding(start = 10.dp, end = 20.dp),
            )
        }

        Text(
            title,
            fontSize = 18.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 100.dp, end = 100.dp)
                .align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(50.dp),
            contentAlignment = Alignment.Center
        ) {
            trailingContent?.invoke(this)
        }
    }
}