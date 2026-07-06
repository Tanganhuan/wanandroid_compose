package com.go.mine.ui.widget

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.core.net.toUri
import com.blankj.utilcode.util.ToastUtils
import com.go.common.BaseApplication
import com.go.common.R
import com.go.common.extension.parseHtmlEntities
import com.go.common.http.HttpConfig
import com.go.mine.data.ArticleBean
import kotlinx.coroutines.launch
import kotlin.text.ifEmpty
import kotlin.text.isNotEmpty

@Composable
fun ArticleDropdownMenus(
    expanded: Boolean,
    offset: DpOffset = DpOffset.Zero,
    article: ArticleBean,
    canCollect: Boolean,
    isCollect: Boolean,
    onSearch: ((String) -> Unit)?,
    onDismissRequest:()->Unit,
    onCollect:(Int)->Unit,
    extradDropdownMenuItems: (@Composable ColumnScope.(ArticleBean,onDismissRequest:()->Unit) -> Unit)?=null,
) {

    DropdownMenu(
        offset = offset,
        expanded = expanded,
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        if(extradDropdownMenuItems != null) {
            extradDropdownMenuItems(article,onDismissRequest)
        }

        if (article.title.isNotEmpty() && article.link.isNotEmpty()) {

            if (canCollect) {
                if(isCollect) {
                    UncollectDropdownMenuItem(articleBean = article, onUncollect = onCollect)
                } else {
                    CollectDropdownMenuItem(
                        id = article.id,
                        onCollect = onCollect,
                    )
                }
            }

            ShareDropdownMenuItem(
                title = article.title,
                link = HttpConfig.handleWanandroidUrl(article.link),
                onItemClick = onDismissRequest
            )
        }

        if (article.link.startsWith(HttpConfig.HTTP_PROTOCOL)) {
            ClipboardDropdownMenuItem(
                label = stringResource(R.string.copy_link),
                copyText = HttpConfig.handleWanandroidUrl(article.link),
                onItemClick = onDismissRequest
            )
            ClipboardDropdownMenuItem(
                label = stringResource(R.string.copy_title),
                copyText = article.title.parseHtmlEntities().toString(),
                onItemClick = onDismissRequest
            )
            if(article.shareUser.ifEmpty { article.author }.isNotEmpty()) {
                ClipboardDropdownMenuItem(
                    label = stringResource(R.string.copy_author_name),
                    copyText = article.shareUser.ifEmpty { article.author },
                    onItemClick = onDismissRequest
                )

                if(onSearch != null) {
                    SearchAuthorDropdownMenuItem(
                        onSearch = { searchKey ->
                            onDismissRequest()
                            onSearch(searchKey)
                        },
                        searchKeyword = article.author.ifEmpty { article.shareUser },
                    )
                }
            }

            OpenInBrowserDropdownMenuItem(
                link = HttpConfig.handleWanandroidUrl(article.link),
                onItemClick = onDismissRequest
            )
        }
    }
}

@Composable
fun WebViewArticleDropdownMenus(
    expanded: Boolean,
    link: String,
    title: String,
    onItemClick:()->Unit
) {
    DropdownMenu(
        offset = DpOffset.Zero,
        expanded = expanded,
        onDismissRequest = {
            onItemClick()
        }
    ) {
        ShareDropdownMenuItem(
            title = title,
            link = link,
            onItemClick = onItemClick
        )
        ClipboardDropdownMenuItem(
            label = stringResource(R.string.copy_link),
            copyText = link,
            onItemClick = onItemClick
        )
        OpenInBrowserDropdownMenuItem(
            link = link,
            onItemClick = onItemClick
        )

    }
}

@Composable
private fun CollectDropdownMenuItem(
    id:Int,
    onCollect: (Int) -> Unit,
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.collect)) },
        onClick = {
            onCollect(id)
        },
    )
}

@Composable
fun UncollectDropdownMenuItem(
    articleBean: ArticleBean,
    onUncollect: (Int) -> Unit,
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.un_collect)) },
        onClick = {
            onUncollect(articleBean.id)
        },
    )
}



@Composable
private fun SearchAuthorDropdownMenuItem(
    searchKeyword:String,
    onSearch: (String) -> Unit,
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.search_author_s_articles)) },
        onClick = {
            onSearch(searchKeyword)
        }
    )
}

@Composable
private fun OpenInBrowserDropdownMenuItem(link: String,onItemClick: () -> Unit) {
    val context = LocalContext.current
    DropdownMenuItem(
        text = { Text(stringResource(R.string.open_in_browser)) },
        onClick = {
            onItemClick()
            runCatching {
                val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                context.startActivity(intent)
            }
        }
    )
}

@Composable
private fun ShareDropdownMenuItem(
    title: String,
    link: String,
    onItemClick: () -> Unit
) {
    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ToastUtils.showLong(BaseApplication.Instance.getString(R.string.share_success))
        }
    }

    DropdownMenuItem(
        text = { Text(stringResource(R.string.share)) },
        onClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, link)
            }
            shareLauncher.launch(
                Intent.createChooser(
                    intent,
                    BaseApplication.Instance.getString(R.string.choose_the_sharing_method)
                )
            )
            onItemClick()
        }
    )
}

@Composable
private fun ClipboardDropdownMenuItem(label:String,copyText:String, onItemClick: () -> Unit) {
    val localClipboard = LocalClipboard.current
    val rememberCoroutineScope = rememberCoroutineScope()
    DropdownMenuItem(
        text = { Text(label) },
        onClick = {
            rememberCoroutineScope.launch {
                val newPlainText = ClipData.newPlainText(copyText, copyText)
                localClipboard.setClipEntry(ClipEntry(newPlainText))
                onItemClick()
            }
        }
    )
}