package com.go.wanandroid.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.go.wanandroid.nav.NavBarItem
import com.go.wanandroid.nav.TOP_LEVEL_ROUTES


@Preview
@Composable
fun MainBottomBarPreview() {
    MainBottomBar(TOP_LEVEL_ROUTES.first().first,{})
}
@Composable
fun MainBottomBar(currentNavKey: NavKey,onItemClick:(NavKey)->Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement= Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TOP_LEVEL_ROUTES.forEach { (key,value) ->
                MainBottomBarItem(value, {
                    onItemClick(key)
                },key == currentNavKey)
            }
        }
    }
}

@Composable
private fun RowScope.MainBottomBarItem(
    item: NavBarItem,
    onItemClick: (NavBarItem) -> Unit,
    selected: Boolean,
) {

    Column(
        modifier = Modifier.clickable {
                onItemClick(item)
            }.padding(3.dp)
            .weight(1f),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val label = stringResource(item.label)
        val color = if (selected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.secondary

        val icon = if(!selected) item.icon else item.selectedIcon

        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = color,
        )
        Spacer(modifier = Modifier.size(2.dp))
        Text(label, color = color, fontSize = 10.sp)
    }
}