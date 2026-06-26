package com.abra.presentation.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AbraColorScheme: ColorScheme =
    lightColorScheme(
        primary = Color(0xFF0E5E5A),
        onPrimary = Color.White,
        secondary = Color(0xFF5D5A2F),
        onSecondary = Color.White,
        tertiary = Color(0xFF7A4E2A),
        background = Color(0xFFFBFCF8),
        surface = Color(0xFFFBFCF8),
        surfaceContainer = Color(0xFFF0F3ED),
        onSurface = Color(0xFF1C1F1B),
        onSurfaceVariant = Color(0xFF5B625C),
        error = Color(0xFFBA1A1A),
    )

@Composable
fun AbraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AbraColorScheme,
        typography = MaterialTheme.typography,
        content = content,
    )
}
