package com.hydrogen.browser.model

import androidx.compose.ui.graphics.Color

enum class SearchEngine(val label: String, val searchUrl: String) {
    BING("必应", "\[suspicious link removed\]"),
    QUARK("夸克", "https://quark.sm.cn/s?q="),
    METASO("秘塔", "https://metaso.cn/?q="),
    GOOGLE("谷歌", "https://www.google.com/search?q=")
}

enum class ScreenMode {
    HOME, WEB, SCRIPTS
}

data class HomeShortcut(
    val title: String,
    val url: String,
    val badge: String,
    val tint: Color
)

data class BrowserTab(
    val id: Long,
    val title: String,
    val url: String
)

data class UserScriptBlock(
    val id: Long,
    val name: String,
    val matchPattern: String,
    val description: String,
    val code: String,
    val enabled: Boolean = true,
    val isWide: Boolean = false,
    val hitCount: Int = 0,
    val cardColor: Color = Color(0xFFEFF6FF)
)

data class SniffedMedia(
    val url: String,
    val mimeType: String
)
