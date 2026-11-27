package com.hydrogen.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydrogen.browser.ui.theme.HydrogenText

@Composable
fun FloatingBottomBar(
    tabCount: Int,
    onBack: () -> Unit,
    onMenu: () -> Unit,
    onHome: () -> Unit,
    onTabs: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    // VIA 悬浮毛玻璃胶囊栏：返回 | 菜单 | 主页 | 标签 | 设置
    Row(
        modifier = modifier
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .fillMaxWidth()
            .height(56.dp)
            .shadow(16.dp, RoundedCornerShape(28.dp), ambientColor = Color(0x220F172A))
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.88f))
            .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(28.dp))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BarButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回", tint = HydrogenText)
        }
        BarButton(onClick = onMenu) {
            Icon(Icons.Rounded.Menu, contentDescription = "菜单", tint = HydrogenText)
        }
        BarButton(onClick = onHome) {
            Icon(Icons.Rounded.Home, contentDescription = "主页", tint = HydrogenText)
        }
        BarButton(onClick = onTabs) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Layers, contentDescription = "标签", tint = HydrogenText)
                Text(
                    text = tabCount.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydrogenText,
                    modifier = Modifier.offset(x = 9.dp, y = (-7).dp)
                )
            }
        }
        BarButton(onClick = onSettings) {
            Icon(Icons.Rounded.Settings, contentDescription = "设置", tint = HydrogenText)
        }
    }
}

@Composable
private fun BarButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
