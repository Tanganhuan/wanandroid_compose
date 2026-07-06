package com.go.mine.ui.widget

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.go.common.BaseApplication
import com.go.common.R
import com.go.common.extension.parseHtmlEntities
import com.go.common.extension.toDp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.go.common.data.RequestState
import com.go.common.navigation3.LocalNavigator
import com.go.common.widget.RequestStateDialog
import com.go.mine.data.ArticleBean
import com.go.mine.nav.LoginScreenNavKey
import com.go.mine.viewmodel.UserViewModel

@Preview(apiLevel = 36)
@Composable
fun ArticleItemWidgetPreview() {
    ArticleItemWidget(
        isTopArticle = true,
        article = ArticleBean(
            author = "知乎",
            niceDate = "2026-04-28 16:15", title = "Android开发行情跌到谷底了。。",
            chapterName = "干货资源", superChapterName = "干货资源"
        ),
        onPicClick = {},
        onItemClick = {},
        onSearch = {}
    )
}
private const val TAG = "ArticleItemWidgetTAG"
@Composable
fun ArticleItemWidget(
    isTopArticle: Boolean = false,
    canCollect: Boolean = true,
    visibleCollectRow: Boolean = true,
    article: ArticleBean,
    onPicClick: ((String) -> Unit)? = null,
    onItemClick: (String) -> Unit,
    onSearch:((String)->Unit)? = null,
    extradDropdownMenuItems: (@Composable ColumnScope.(ArticleBean,onDismissRequest:()->Unit) -> Unit)?=null,
) {
    val rememberCoroutineScope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val userViewModel = viewModel<UserViewModel>(
        factory = UserViewModel.Factory,
        viewModelStoreOwner = LocalActivity.current as ViewModelStoreOwner
    )
    var requestState: RequestState by remember {
        mutableStateOf(RequestState.Dismiss)
    }
    RequestStateDialog(state = requestState)
    val isCollect = userViewModel.articleCollectState[article.id]?: article.collect
    val localNavigator = LocalNavigator.current
    val onCollect:(Int)->Unit = { id  ->
        expanded = false
        if (userViewModel.isLogined()) {
            if(!isCollect) {
                userViewModel.addCollectArticle(
                    id = id,
                    scope = rememberCoroutineScope
                ) {
                    requestState = it
                }
            } else {
                userViewModel.unCollectArticle(
                    id = id,
                    onEvent = {
                        requestState = it
                    },
                    scope = rememberCoroutineScope
                )
            }

        } else {
            ToastUtils.showLong(BaseApplication.Instance.getString(R.string.please_login_first))
            localNavigator.navigate(LoginScreenNavKey())
        }
    }

    Box {
        ArticleDropdownMenus(
            expanded = expanded,
            offset = DpOffset(x = offset.x.toDp(), y = 0.dp),
            article = article,
            canCollect = canCollect,
            isCollect = isCollect,
            onSearch = onSearch,
            onCollect = onCollect,
            onDismissRequest = {
                expanded = false
            },
            extradDropdownMenuItems = extradDropdownMenuItems
        )

        ArticleItemWidgetCore(
            onItemClick = onItemClick,
            article = article,
            isTopArticle = isTopArticle,
            isCollect = isCollect,
            onPicClick = onPicClick,
            visibleCollectRow = visibleCollectRow,
            canCollect = canCollect,
            onCollect = onCollect,
            onLongPress = {
                offset = it
                expanded = true
            }
        )
    }
}

@Composable
private fun ArticleItemWidgetCore(
    visibleCollectRow: Boolean,
    article: ArticleBean,
    isTopArticle: Boolean,
    canCollect: Boolean,
    isCollect: Boolean,
    onLongPress: ((Offset))->Unit,
    onItemClick: (String) -> Unit,
    onPicClick: ((String) -> Unit)?,
    onCollect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    onLongPress(it)
                    LogUtils.d(TAG, "detectTapGestures onLongPress offset x:${it.x}\ty:${it.y}")
                }, onTap = {
                    LogUtils.d(TAG, "detectTapGestures onTap offset x:${it.x}\ty:${it.y}")
                    onItemClick.invoke(article.link)
                })
            }
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        val contentColor = LocalContentColor.current.copy(alpha = 0.8f)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 8.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (article.shareUser.isNotEmpty()) {
                    Text(
                        article.shareUser,
                        modifier = Modifier.padding(end = 4.dp),
                        color = contentColor
                    )
                }
                if (article.author.isNotEmpty()) {
                    Text(article.author, color = contentColor)
                }
                if (isTopArticle) {
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(start = 4.dp, end = 4.dp)

                    ) {
                        Text(
                            text = stringResource(R.string.top_article),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                if (article.fresh) {
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .border(
                                width = 2.dp,
                                color = Color.Red,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(start = 4.dp, end = 4.dp)

                    ) {
                        Text(
                            text = stringResource(R.string.new_article),
                            color = Color.Red,
                        )
                    }
                }
            }
            Text(article.niceDate, color = contentColor)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 2.dp)
        ) {
            if (article.envelopePic.isNotEmpty()) {
                val painterResource = painterResource(R.drawable.default_project_img)
                AsyncImage(
                    model = article.envelopePic,
                    placeholder = painterResource,
                    error = painterResource,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 10.dp)
                        .clickable {
                            if (onPicClick == null) {
                                onItemClick(article.link)
                            } else {
                                onPicClick.invoke(article.envelopePic)
                            }

                        },
                    contentScale = ContentScale.FillWidth
                )
            }
            Column {
                Text(
                    text = article.title.parseHtmlEntities().text,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (article.desc.isNotEmpty()) {
                    Text(
                        text = article.desc.parseHtmlEntities().text,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4,
                    )
                }
            }

        }

        if (visibleCollectRow) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text =
                    if (article.superChapterName.isNotEmpty() && article.chapterName.isNotEmpty()) {
                        "${article.superChapterName.parseHtmlEntities()}·${article.chapterName.parseHtmlEntities()}"
                    } else if (article.superChapterName.isNotEmpty()) {
                        "${article.superChapterName.parseHtmlEntities()}"
                    } else {
                        "${article.chapterName.parseHtmlEntities()}"
                    }

                Text(
                    text = text,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )

                if (canCollect) {
                    Icon(
                        painter = painterResource(if(isCollect) R.drawable.favorite_fill else R.drawable.favorite),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                onCollect(article.id)
                            }
                            .size(18.dp))
                }
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
        HorizontalDivider()
    }
}
