package com.hydrogen.browser

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hydrogen.browser.engine.*
import com.hydrogen.browser.model.*
import com.hydrogen.browser.ui.components.ControlCenterSheet
import com.hydrogen.browser.ui.components.FloatingBottomBar
import com.hydrogen.browser.ui.screens.HomeScreen
import com.hydrogen.browser.ui.screens.ScriptBentoScreen
import com.hydrogen.browser.ui.theme.HydrogenBg
import com.hydrogen.browser.ui.theme.HydrogenTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HydrogenTheme {
                HydrogenApp()
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HydrogenApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var screenMode by remember { mutableStateOf(ScreenMode.HOME) }
    var searchEngine by remember { mutableStateOf(SearchEngine.BING) }
    var tabs by remember { mutableStateOf(listOf(BrowserTab(1L, "主页", ""))) }
    var activeTabId by remember { mutableStateOf(1L) }
    var scripts by remember { mutableStateOf(UserScriptEngine.defaultBlocks()) }

    var showControlSheet by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showTabsDialog by remember { mutableStateOf(false) }
    var incognito by remember { mutableStateOf(false) }
    var adBlockOn by remember { mutableStateOf(true) }
    var desktopUa by remember { mutableStateOf(false) }
    var novelMode by remember { mutableStateOf(false) }

    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var blockedCount by remember { mutableStateOf(0) }

    fun navigateTo(input: String) {
        val trimmed = input.trim()
        val url = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else if (trimmed.contains(".") && !trimmed.contains(" ")) {
            "https://$trimmed"
        } else {
            searchEngine.searchUrl + java.net.URLEncoder.encode(trimmed, "UTF-8")
        }
        tabs = tabs.map { if (it.id == activeTabId) it.copy(url = url, title = trimmed) else it }
        screenMode = ScreenMode.WEB
        webViewRef?.loadUrl(url)
    }

    Box(modifier = Modifier.fillMaxSize().background(HydrogenBg)) {
        when (screenMode) {
            ScreenMode.HOME -> HomeScreen(
                adBlockedCount = blockedCount,
                onSubmitQuery = { navigateTo(it) }
            )
            ScreenMode.SCRIPTS -> ScriptBentoScreen(
                scripts = scripts,
                onToggleScript = { id ->
                    scripts = scripts.map { if (it.id == id) it.copy(enabled = !it.enabled) else it }
                },
                onImportGreasyFork = { url ->
                    scope.launch {
                        val imported = UserScriptEngine.fetchFromGreasyFork(url)
                        if (imported != null) {
                            scripts = listOf(imported) + scripts
                            Toast.makeText(context, "已安装：${imported.name}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "脚本拉取失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onGenerateAiScript = { prompt ->
                    val block = AiAssistantEngine.generateScriptFromPrompt(prompt)
                    scripts = listOf(block) + scripts
                    Toast.makeText(context, "已生成方块：${block.name}", Toast.LENGTH_SHORT).show()
                }
            )
            ScreenMode.WEB -> {
                val activeUrl = tabs.firstOrNull { it.id == activeTabId }?.url ?: ""
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 36.dp, bottom = 84.dp)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount > 55 && webViewRef?.canGoBack() == true) webViewRef?.goBack()
                                if (dragAmount < -55 && webViewRef?.canGoForward() == true) webViewRef?.goForward()
                            }
                        }
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = !incognito
                                webViewClient = object : WebViewClient() {
                                    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                                        val reqUrl = request?.url?.toString() ?: return null
                                        MediaSniffer.inspectUrl(reqUrl)
                                        if (adBlockOn && AdBlockEngine.shouldBlock(reqUrl)) {
                                            blockedCount = AdBlockEngine.blockedCount
                                            return AdBlockEngine.emptyResponse()
                                        }
                                        return super.shouldInterceptRequest(view, request)
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        if (adBlockOn) view?.evaluateJavascript(AdBlockEngine.buildHideAdsJs(), null)
                                        val pageUrl = url ?: return
                                        scripts.filter { it.enabled && UserScriptEngine.matchesUrl(it.matchPattern, pageUrl) }
                                            .forEach { script ->
                                                view?.evaluateJavascript(script.code, null)
                                            }
                                    }
                                }
                                webViewRef = this
                                if (activeUrl.isNotEmpty()) loadUrl(activeUrl)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // VIA 悬浮毛玻璃胶囊栏：返回 | 菜单 | 主页 | 标签 | 设置
        FloatingBottomBar(
            tabCount = tabs.size,
            onBack = {
                if (screenMode == ScreenMode.WEB && webViewRef?.canGoBack() == true) {
                    webViewRef?.goBack()
                } else {
                    screenMode = ScreenMode.HOME
                }
            },
            onMenu = { showControlSheet = true },
            onHome = { screenMode = ScreenMode.HOME },
            onTabs = { showTabsDialog = true },
            onSettings = { showSettingsDialog = true },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showControlSheet) {
        ControlCenterSheet(
            incognito = incognito,
            adBlockOn = adBlockOn,
            desktopUa = desktopUa,
            novelMode = novelMode,
            onDismiss = { showControlSheet = false },
            onToggleIncognito = { incognito = !incognito },
            onToggleAdBlock = {
                adBlockOn = !adBlockOn
                AdBlockEngine.strictMode = adBlockOn
            },
            onOpenScripts = { screenMode = ScreenMode.SCRIPTS },
            onToggleDesktopUa = {
                desktopUa = !desktopUa
                webViewRef?.settings?.userAgentString = if (desktopUa)
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/128.0.0.0 Safari/537.36"
                else null
                webViewRef?.reload()
            },
            onPickAdElement = {
                webViewRef?.evaluateJavascript(AdBlockEngine.elementPickerJs, null)
                Toast.makeText(context, "点击网页上任意广告即可屏蔽", Toast.LENGTH_SHORT).show()
            },
            onBilingualTranslate = {
                webViewRef?.evaluateJavascript(AiAssistantEngine.bilingualTranslateJs, null)
            },
            onToggleNovelMode = {
                novelMode = !novelMode
                Toast.makeText(context, if (novelMode) "小说阅读模式已开启" else "已退出小说模式", Toast.LENGTH_SHORT).show()
            },
            onShowSniffedMedia = {
                val count = MediaSniffer.detected.size
                Toast.makeText(context, "已嗅探到 $count 个 M3U8/MP4 媒体资源", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showTabsDialog) {
        AlertDialog(
            onDismissRequest = { showTabsDialog = false },
            title = { Text("标签页 (${tabs.size})") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tabs.forEach { tab ->
                        TextButton(onClick = {
                            activeTabId = tab.id
                            screenMode = if (tab.url.isEmpty()) ScreenMode.HOME else ScreenMode.WEB
                            showTabsDialog = false
                        }) { Text(tab.title.ifEmpty { "新标签页" }) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val newId = System.currentTimeMillis()
                    tabs = tabs + BrowserTab(newId, "新标签页", "")
                    activeTabId = newId
                    screenMode = ScreenMode.HOME
                    showTabsDialog = false
                }) { Text("+ 新建标签") }
            }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("氢浏览器设置") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("默认搜索引擎：${searchEngine.label}")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SearchEngine.entries.forEach { engine ->
                            FilterChip(
                                selected = searchEngine == engine,
                                onClick = { searchEngine = engine },
                                label = { Text(engine.label) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) { Text("完成") }
            }
        )
    }
}
