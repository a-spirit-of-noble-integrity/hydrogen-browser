package com.hydrogen.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydrogen.browser.ui.theme.HydrogenBlue
import com.hydrogen.browser.ui.theme.HydrogenText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlCenterSheet(
    incognito: Boolean,
    adBlockOn: Boolean,
    desktopUa: Boolean,
    novelMode: Boolean,
    onDismiss: () -> Unit,
    onToggleIncognito: () -> Unit,
    onToggleAdBlock: () -> Unit,
    onOpenScripts: () -> Unit,
    onToggleDesktopUa: () -> Unit,
    onPickAdElement: () -> Unit,
    onBilingualTranslate: () -> Unit,
    onToggleNovelMode: () -> Unit,
    onShowSniffedMedia: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
            Text(
                text = "控制中心",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = HydrogenText,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SheetTile("无痕模式", Icons.Rounded.VisibilityOff, incognito, Modifier.weight(1f), onToggleIncognito)
                SheetTile("去广告", Icons.Rounded.Shield, adBlockOn, Modifier.weight(1f), onToggleAdBlock)
                SheetTile("油猴方块", Icons.Rounded.GridView, true, Modifier.weight(1f)) {
                    onDismiss(); onOpenScripts()
                }
                SheetTile("电脑版", Icons.Rounded.DesktopWindows, desktopUa, Modifier.weight(1f), onToggleDesktopUa)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SheetTile("标记广告", Icons.Rounded.TouchApp, false, Modifier.weight(1f)) {
                    onDismiss(); onPickAdElement()
                }
                SheetTile("双语翻译", Icons.Rounded.Translate, false, Modifier.weight(1f)) {
                    onDismiss(); onBilingualTranslate()
                }
                SheetTile("小说模式", Icons.Rounded.MenuBook, novelMode, Modifier.weight(1f), onToggleNovelMode)
                SheetTile("视频嗅探", Icons.Rounded.VideoLibrary, false, Modifier.weight(1f)) {
                    onDismiss(); onShowSniffedMedia()
                }
            }
        }
    }
}

@Composable
private fun SheetTile(
    label: String,
    icon: ImageVector,
    active: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (active) Color(0xFFEFF6FF) else Color(0xFFF1F5F9))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = if (active) HydrogenBlue else HydrogenText)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (active) HydrogenBlue else HydrogenText
        )
    }
}
