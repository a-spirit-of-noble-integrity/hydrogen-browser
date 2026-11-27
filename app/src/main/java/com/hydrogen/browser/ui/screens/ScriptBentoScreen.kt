package com.hydrogen.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydrogen.browser.model.UserScriptBlock
import com.hydrogen.browser.ui.theme.*

@Composable
fun ScriptBentoScreen(
    scripts: List<UserScriptBlock>,
    onToggleScript: (Long) -> Unit,
    onImportGreasyFork: (String) -> Unit,
    onGenerateAiScript: (String) -> Unit
) {
    var showImportDialog by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HydrogenBg)
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp, bottom = 92.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("油猴方块", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = HydrogenText)
                Text("Bento 模块化脚本引擎 · GreasyFork / AI", fontSize = 12.sp, color = HydrogenMuted)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalIconButton(onClick = { inputText = ""; showAiDialog = true }) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI 生成脚本", tint = HydrogenBlue)
                }
                FilledIconButton(
                    onClick = { inputText = ""; showImportDialog = true },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = HydrogenBlue)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "导入脚本")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = scripts,
                key = { it.id },
                span = { GridItemSpan(if (it.isWide) 2 else 1) }
            ) { block ->
                BentoScriptCard(block = block, onToggle = { onToggleScript(block.id) })
            }
        }
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("从 GreasyFork 导入脚本") },
            text = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("粘贴 .user.js 直链 URL") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (inputText.isNotBlank()) onImportGreasyFork(inputText)
                    showImportDialog = false
                }) { Text("安装") }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) { Text("取消") }
            }
        )
    }

    if (showAiDialog) {
        AlertDialog(
            onDismissRequest = { showAiDialog = false },
            title = { Text("AI 一句话写油猴脚本") },
            text = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("如：隐藏网页右侧边栏") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (inputText.isNotBlank()) onGenerateAiScript(inputText)
                    showAiDialog = false
                }) { Text("生成方块") }
            },
            dismissButton = {
                TextButton(onClick = { showAiDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun BentoScriptCard(block: UserScriptBlock, onToggle: () -> Unit) {
    val bg = if (block.enabled) block.cardColor.copy(alpha = 0.9f) else Color(0xFFF1F5F9)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 118.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(bg)
            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Code, contentDescription = null, tint = HydrogenBlue, modifier = Modifier.size(18.dp))
            }
            Switch(
                checked = block.enabled,
                onCheckedChange = { onToggle() }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column {
            Text(block.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HydrogenText, maxLines = 1)
            Text("${block.matchPattern} · ${block.hitCount}次", fontSize = 11.sp, color = HydrogenMuted, maxLines = 1)
        }
    }
}
