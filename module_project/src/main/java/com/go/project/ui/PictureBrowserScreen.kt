package com.go.project.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.panpf.zoomimage.CoilZoomAsyncImage
import com.go.common.R
import com.go.common.navigation3.EmptyNavigator
import com.go.common.navigation3.INavigator
import com.go.common.widget.AppTopBackBox

@Preview
@Composable
fun PictureBrowserScreenPreview() {
    PictureBrowserScreen(navigator = EmptyNavigator(),url = "https://img0.baidu.com/it/u=3591665277,2616537962&fm=253&app=138&f=JPEG?w=800&h=1333")
}
@Composable
fun PictureBrowserScreen(navigator: INavigator,url: String) {

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBackBox(title = stringResource(com.go.project.R.string.picture_browser)) {
            navigator.goBack()
        }
        val painterResource = painterResource(R.drawable.default_project_img)
        CoilZoomAsyncImage(
            model = url,
            error = painterResource,
            fallback = painterResource,
            contentDescription = "view image",
            modifier = Modifier.fillMaxSize(),
        )
    }

}