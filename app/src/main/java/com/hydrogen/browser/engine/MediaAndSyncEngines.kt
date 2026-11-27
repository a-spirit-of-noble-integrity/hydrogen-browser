package com.hydrogen.browser.engine

import com.hydrogen.browser.model.SniffedMedia
import com.hydrogen.browser.model.UserScriptBlock
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object MediaSniffer {
    val detected = mutableListOf<SniffedMedia>()

    fun inspectUrl(url: String) {
        val lower = url.lowercase()
        if (lower.contains(".m3u8") || lower.contains(".mp4")) {
            if (detected.none { it.url == url }) {
                detected.add(0, SniffedMedia(url, if (lower.contains(".m3u8")) "HLS/M3U8" else "MP4"))
            }
        }
    }
}

object WebDavSyncManager {
    private val client = OkHttpClient()

    suspend fun uploadBackup(serverUrl: String, user: String, pass: String, jsonPayload: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val req = Request.Builder()
                    .url(serverUrl.trimEnd('/') + "/hydrogen_backup.json")
                    .header("Authorization", Credentials.basic(user, pass))
                    .put(jsonPayload.toRequestBody("application/json".toMediaType()))
                    .build()
                client.newCall(req).execute().isSuccessful
            }.getOrDefault(false)
        }
}

object AiAssistantEngine {
    fun generateScriptFromPrompt(prompt: String): UserScriptBlock {
        val clean = prompt.trim()
        val js = when {
            clean.contains("隐藏") || clean.contains("侧边栏") ->
                "document.querySelectorAll('aside, .sidebar, [class*="side"]').forEach(e => e.style.display = 'none');"
            clean.contains("夜间") || clean.contains("深色") ->
                "document.documentElement.style.filter = 'invert(0.9) hue-rotate(180deg)';"
            else ->
                "// AI 生成脚本: $clean\nconsole.log('Hydrogen AI Script Active');"
        }
        return UserScriptBlock(
            id = System.currentTimeMillis(),
            name = clean.take(12).ifEmpty { "AI 定制脚本" },
            matchPattern = "*://*/*",
            description = "由氢 AI 一句话生成",
            code = js,
            enabled = true,
            cardColor = Color(0xFFEFF6FF)
        )
    }

    val bilingualTranslateJs: String = """
        (function() {
            document.querySelectorAll('p, h1, h2, h3').forEach(function(el) {
                if (!el.dataset.hTranslated && el.innerText.trim().length > 10) {
                    el.dataset.hTranslated = '1';
                    var sub = document.createElement('div');
                    sub.style.cssText = 'color:#2563EB;font-size:0.9em;margin-top:4px;';
                    sub.innerText = '[AI 译文占位] ' + el.innerText.slice(0, 60) + '...';
                    el.appendChild(sub);
                }
            });
        })();
    """.trimIndent()
}
