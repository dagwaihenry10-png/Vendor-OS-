package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VendorGreen,
    onPrimary = Color.Black,
    primaryContainer = VendorGreenDark,
    onPrimaryContainer = Color.White,
    secondary = SkillsOrange,
    onSecondary = Color.White,
    secondaryContainer = SkillsAmber,
    onSecondaryContainer = Color.Black,
    tertiary = SuperBlueLight,
    onTertiary = Color.White,
    background = NavyBg,
    onBackground = TextPrimary,
    surface = NavyCard,
    onSurface = TextPrimary,
    surfaceVariant = NavyCardLight,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = VendorGreenDark,
    onPrimary = Color.White,
    primaryContainer = VendorGreen,
    onPrimaryContainer = Color.Black,
    secondary = SkillsOrange,
    onSecondary = Color.White,
    secondaryContainer = SkillsAmber,
    onSecondaryContainer = Color.Black,
    tertiary = SuperBlue,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun VendorOSTheme(
    darkTheme: Boolean = true, // Default to sleek modern dark theme for VendorOS
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
