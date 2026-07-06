package com.go.common.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.go.common.R

sealed class ItemBoxData {
    data class ClickableItemData(val title:String, val result:String?=null, val onClick:()->Unit): ItemBoxData()
    data class RadioItemData(val title: String, val checked: Boolean, val onClick:()->Unit): ItemBoxData()
    data class SwitchItemData(val title: String, val desc: String, val checked: Boolean, val onCheckedChange: ((Boolean) -> Unit)?): ItemBoxData()
}


@Preview
@Composable
fun ClickableItemBoxPreview() {
    Column{

        val items = listOf<ItemBoxData>(
            ItemBoxData.ClickableItemData(title = "测试1", result = "已打开", onClick = {}),
            ItemBoxData.ClickableItemData(title = "测试2", result = "已打开", onClick = {}),
            ItemBoxData.ClickableItemData(title = "测试3", result = "已打开", onClick = {}),
        )
        ColumnItemsBox(items)

        val items2 = listOf<ItemBoxData>(
            ItemBoxData.RadioItemData(title = "测试1", checked = true, onClick = {}),
            ItemBoxData.RadioItemData(title = "测试2", checked = false, onClick = {}),
            ItemBoxData.RadioItemData(title = "测试3", checked = true, onClick = {}),
        )
        ColumnItemsBox(items2)

        val items3 = listOf<ItemBoxData>(
            ItemBoxData.SwitchItemData(title = "测试1", desc="测试1",checked = true, onCheckedChange = {}),
            ItemBoxData.SwitchItemData(title = "测试2", desc="测试1",checked = false, onCheckedChange = {}),
            ItemBoxData.SwitchItemData(title = "测试3", desc="测试1",checked = true, onCheckedChange = {}),
        )
        ColumnItemsBox(items3)
    }
}

@Composable
fun ClickableItemBox(data:ItemBoxData.ClickableItemData) {
    Box(
        modifier = Modifier.clickable{data.onClick.invoke()}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(data.title,modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp))

            if(data.result != null) {
                Text(data.result, fontSize = 12.sp, modifier = Modifier.padding(end = 10.dp))
            }
            Icon(painter= painterResource(R.drawable.keyboard_arrow_right),
                contentDescription = data.title)
        }
    }
}

@Composable
fun RadioItemBox(data:ItemBoxData.RadioItemData) {
    Box(
        modifier = Modifier.clickable{ data.onClick.invoke()}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(data.title,modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp))
            RadioButton(selected = data.checked, onClick = data.onClick)
        }
    }
}


@Composable
fun SwitchItemBox(data:ItemBoxData.SwitchItemData) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 10.dp,top = 8.dp, bottom = 8.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier
                .weight(1f)
                ) {
                Text(data.title)
                if(data.desc.isNotEmpty()) {
                    Text(data.desc, fontSize = 10.sp, color = LocalContentColor.current.copy(alpha = 0.8f))
                }
            }

            Switch(checked = data.checked, onCheckedChange = data.onCheckedChange)
        }
    }
}

@Composable
fun ColumnItemsBox(items: List<ItemBoxData>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        val hasItemSpacer = items.size > 1
        items.forEachIndexed { index, data ->
            when(data) {
                is ItemBoxData.ClickableItemData -> {
                    ClickableItemBox(data)
                }

                is ItemBoxData.RadioItemData -> {
                    RadioItemBox(data)
                }

                is ItemBoxData.SwitchItemData -> {
                    SwitchItemBox(data)
                }
            }
            if(hasItemSpacer && index < items.size-1) {
                ItemSpacer()
            }
        }
    }
}

@Composable
private fun ItemSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}
