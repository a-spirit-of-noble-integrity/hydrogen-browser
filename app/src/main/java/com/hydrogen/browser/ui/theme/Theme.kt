package com.hydrogen.browser.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val HydrogenBlue = Color(0xFF2563EB)
val HydrogenBg = Color(0xFFFAFBFC)
val HydrogenSurface = Color(0xFFFFFFFF)
val HydrogenText = Color(0xFF0F172A)
val HydrogenMuted = Color(0xFF64748B)
val HydrogenBorder = Color(0xFFE2E8F0)

private val LightColors = lightColorScheme(
    primary = HydrogenBlue,
    background = HydrogenBg,
    surface = HydrogenSurface,
    onPrimary = Color.White,
    onBackground = HydrogenText,
    onSurface = HydrogenText
)

@Composable
fun HydrogenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
