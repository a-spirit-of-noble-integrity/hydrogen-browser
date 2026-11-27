package com.hydrogen.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydrogen.browser.model.HomeShortcut
import com.hydrogen.browser.ui.theme.*

@Composable
fun HomeScreen(
    adBlockedCount: Int,
    onSubmitQuery: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    // 严格按规格书：首页捷径仅保留「必应」「谷歌」微卡片
    val shortcuts = remember {
        listOf(
            HomeShortcut("必应", "https://www.bing.com", "B", Color(0xFF0EA5E9)),
            HomeShortcut("谷歌", "https://www.google.com", "G", HydrogenBlue)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HydrogenBg)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Hydrogen", fontSize = 12.sp, color = HydrogenMuted)
            Text("已拦截 $adBlockedCount 次广告", fontSize = 12.sp, color = Color(0xFF10B981))
        }

        Spacer(modifier = Modifier.height(72.dp))
        Text(
            text = "Hydrogen",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = HydrogenText,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(28.dp))

        // 夸克式极简单行搜索框
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(12.dp, RoundedCornerShape(27.dp), ambientColor = Color(0x140F172A))
                .clip(RoundedCornerShape(27.dp))
                .background(Color.White)
                .border(1.dp, HydrogenBorder, RoundedCornerShape(27.dp))
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = HydrogenBlue)
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (query.isEmpty()) {
                    Text("搜索或输入网址", color = HydrogenMuted, fontSize = 15.sp)
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = HydrogenText, fontSize = 15.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (query.isNotBlank()) onSubmitQuery(query)
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(34.dp))

        // 微卡片底框快捷入口：仅「必应」「谷歌」
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            shortcuts.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSubmitQuery(item.url) }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x140F172A))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, HydrogenBorder, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.badge,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.tint
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(item.title, fontSize = 12.sp, color = HydrogenText, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
