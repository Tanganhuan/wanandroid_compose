package com.go.mine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blankj.utilcode.util.ToastUtils
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.go.common.datastore.AppThemeStateDataStore
import com.go.common.extension.hex
import com.go.common.navigation3.EmptyNavigator
import com.go.common.navigation3.INavigator
import com.go.common.theme.emptyAppThemeState
import com.go.common.widget.AppTopBackBox
import com.go.common.widget.ColumnItemsBox
import com.go.common.widget.ItemBoxData
import com.go.mine.R
import kotlinx.coroutines.launch

@Preview
@Composable
fun ThemeSettingScreenPreview() {
    ThemeSettingScreen(EmptyNavigator())
}

private const val TAG = "ThemeSettingScreenTAG"

@Composable
fun ThemeSettingScreen(navigator: INavigator) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val rememberCoroutineScope = rememberCoroutineScope()
    var colorEnvelopeState by remember {
        mutableStateOf(
            ColorEnvelope(
                color = primaryColor,
                hexCode = primaryColor.hex,
                fromUser = false
            )
        )
    }

    val appThemeState = AppThemeStateDataStore.read()
        .collectAsStateWithLifecycle(initialValue = emptyAppThemeState).value

    Column {
        AppTopBackBox(title = stringResource(R.string.theme_color), trailingContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        if (appThemeState.darkTheme
                            || (appThemeState.darkThemeFollowSystem && isSystemInDarkTheme)
                        ) {
                            ToastUtils.showShort(R.string.theme_color_only_effective_in_light_theme_mode)
                            return@clickable
                        }
                        rememberCoroutineScope.launch {
                            AppThemeStateDataStore.write(appThemeState.copy(primaryColor = colorEnvelopeState.color.value))
                        }
                    }) {
                Text(
                    stringResource(R.string.confirm),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = 20.dp, end = 10.dp),
                )
            }
        }) {
            navigator.goBack()
        }

        val titleBarFollowsThemeColor = listOf<ItemBoxData>(
            ItemBoxData.SwitchItemData(
                title = stringResource(R.string.title_bar_follows_the_theme_color),
                desc = stringResource(R.string.theme_color_only_effective_in_light_theme_mode),
                checked = appThemeState.statusBarFollowPrimaryColor,
            ) {
                rememberCoroutineScope.launch {
                    AppThemeStateDataStore.write(appThemeState.copy(statusBarFollowPrimaryColor = it))
                }

            }
        )

        ColumnItemsBox(titleBarFollowsThemeColor)

        HsvColorPicker(
            modifier = Modifier
                .height(450.dp)
                .padding(10.dp),
            controller = rememberColorPickerController(),
            initialColor = MaterialTheme.colorScheme.primary,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                colorEnvelopeState = colorEnvelope
            },
            onStart = {

            },
            onFinish = {

            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .width(450.dp)
                .padding(10.dp)
        ) {
            TextField(
                value = "#${colorEnvelopeState.hexCode}",
                onValueChange = {

                },
                modifier = Modifier
                    .height(50.dp)
                    .weight(1f),
                enabled = false
            )
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .weight(1f)
                    .background(colorEnvelopeState.color)
            )
        }

    }

}