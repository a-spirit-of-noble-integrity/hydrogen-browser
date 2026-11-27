package com.hydrogen.browser.engine

import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

object AdBlockEngine {
    var strictMode: Boolean = true
    var blockedCount: Int = 0
        private set

    private val blockedHosts = setOf(
        "pos.baidu.com", "cpro.baidu.com", "hm.baidu.com",
        "googleads.g.doubleclick.net", "pagead2.googlesyndication.com",
        "adservice.google.com", "tpc.googlesyndication.com",
        "cnzz.com", "umeng.com", "tanx.com", "mmstat.com",
        "ads.mopub.com", "applovin.com", "unityads.unity3d.com"
    )

    private val blockedPathKeywords = listOf(
        "/ads/", "/adview", "/adserver", "popunder", "beacon.js", "analytics.js"
    )

    private val customCssSelectors = mutableListOf(
        ".open-app-btn", ".download-app-banner", "[class*='ad-banner']",
        "[id*='google_ads']", ".zhihu-open-app", ".bili-open-app"
    )

    fun shouldBlock(url: String): Boolean {
        if (!strictMode) return false
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return false
        val host = uri.host?.lowercase() ?: return false

        if (blockedHosts.any { host == it || host.endsWith(".$it") }) {
            blockedCount++
            return true
        }
        val lower = url.lowercase()
        if (blockedPathKeywords.any { lower.contains(it) }) {
            blockedCount++
            return true
        }
        return false
    }

    fun emptyResponse(): WebResourceResponse =
        WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))

    fun addCustomSelector(selector: String) {
        if (selector.isNotBlank() && !customCssSelectors.contains(selector)) {
            customCssSelectors.add(selector)
        }
    }

    fun buildHideAdsJs(): String {
        val joined = customCssSelectors.joinToString(",")
        return """
            (function() {
                var style = document.getElementById('hydrogen-adblock-style') || document.createElement('style');
                style.id = 'hydrogen-adblock-style';
                style.innerHTML = '$joined { display: none !important; visibility: hidden !important; }';
                (document.head || document.documentElement).appendChild(style);
            })();
        """.trimIndent()
    }

    val elementPickerJs: String = """
        (function() {
            if (window.__hydrogenPickerActive) return;
            window.__hydrogenPickerActive = true;
            var handler = function(e) {
                e.preventDefault();
                e.stopPropagation();
                e.target.style.display = 'none';
                window.__hydrogenPickerActive = false;
                document.removeEventListener('click', handler, true);
            };
            document.addEventListener('click', handler, true);
        })();
    """.trimIndent()
}
