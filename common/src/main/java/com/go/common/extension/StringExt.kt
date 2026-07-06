package com.go.common.extension

import android.text.Html
import androidx.compose.ui.text.AnnotatedString

// 扩展函数：将包含 HTML 实体的字符串转换为 AnnotatedString
fun String.parseHtmlEntities(): AnnotatedString {
    // HtmlCompat.fromHtml 会自动把 &mdash; 等实体解析成对应的真实字符
    val spanned = Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    return AnnotatedString(spanned.toString())
}
