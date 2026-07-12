package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CustomDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonOrchid,
    tertiary = GlowingAqua,
    background = ObsidianBackground,
    surface = DeepSurface,
    surfaceVariant = SurfaceCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onPrimary = ObsidianBackground,
    onSecondary = ObsidianBackground,
    onTertiary = ObsidianBackground,
    outline = BorderColor
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CustomDarkColorScheme,
        typography = Typography,
        content = content
    )
}
