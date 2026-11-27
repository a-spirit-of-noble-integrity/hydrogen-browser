package com.hydrogen.browser.engine

import androidx.compose.ui.graphics.Color
import com.hydrogen.browser.model.UserScriptBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object UserScriptEngine {
    private val client = OkHttpClient()

    fun defaultBlocks(): List<UserScriptBlock> = listOf(
        UserScriptBlock(
            id = 1L,
            name = "全网去广告增强",
            matchPattern = "*://*/*",
            description = "屏蔽悬浮广告、App 唤起条与常见诱导弹窗",
            code = "document.querySelectorAll('[class*=\"open-app\"],[class*=\"ad-\"]').forEach(e=>e.remove());",
            enabled = true,
            isWide = true,
            hitCount = 142,
            cardColor = Color(0xFFEFF6FF)
        ),
        UserScriptBlock(
            id = 2L,
            name = "视频倍速悬浮球",
            matchPattern = "*://*.bilibili.com/*",
            description = "支持 0.5x~3.0x 网页视频倍速控制",
            code = "document.querySelectorAll('video').forEach(v=>{v.playbackRate=1.5;});",
            enabled = true,
            isWide = false,
            hitCount = 38,
            cardColor = Color(0xFFFFF1F2)
        ),
        UserScriptBlock(
            id = 3L,
            name = "沉浸双语翻译",
            matchPattern = "*://*/*",
            description = "段落下方插入轻量对照译文",
            code = "// Hydrogen Bilingual Translate Hook",
            enabled = true,
            isWide = false,
            hitCount = 19,
            cardColor = Color(0xFFECFDF5)
        ),
        UserScriptBlock(
            id = 4L,
            name = "小说自动拼页",
            matchPattern = "*://*.qidian.com/*",
            description = "自动预加载下一章并净化排版",
            code = "// Hydrogen Novel AutoPager",
            enabled = false,
            isWide = false,
            hitCount = 64,
            cardColor = Color(0xFFFFFBEB)
        ),
        UserScriptBlock(
            id = 5L,
            name = "强制复制解限",
            matchPattern = "*://*/*",
            description = "解除网页禁止选中、右键与复制限制",
            code = "document.oncopy=null;document.onselectstart=null;document.body.style.userSelect='text';",
            enabled = true,
            isWide = false,
            hitCount = 11,
            cardColor = Color(0xFFF5F3FF)
        )
    )

    fun matchesUrl(pattern: String, url: String): Boolean {
        if (pattern == "*://*/*" || pattern == "*") return true
        val regexStr = pattern
            .replace(".", "\\.")
            .replace("*", ".*")
        return runCatching { Regex(regexStr).containsMatchIn(url) }.getOrDefault(false)
    }

    suspend fun fetchFromGreasyFork(scriptUrl: String): UserScriptBlock? = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url(scriptUrl).build()
            val body = client.newCall(req).execute().body?.string() ?: return@runCatching null
            val name = Regex("@name\\s+(.+)").find(body)?.groupValues?.get(1)?.trim() ?: "GreasyFork 脚本"
            val match = Regex("@match\\s+(.+)").find(body)?.groupValues?.get(1)?.trim() ?: "*://*/*"
            UserScriptBlock(
                id = System.currentTimeMillis(),
                name = name,
                matchPattern = match,
                description = "来自 GreasyFork",
                code = body,
                enabled = true,
                cardColor = Color(0xFFECFEFF)
            )
        }.getOrNull()
    }
}
