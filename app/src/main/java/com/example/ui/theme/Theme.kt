package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ShopKartAmber,
    onPrimary = ShopKartNavyDark,
    primaryContainer = ShopKartNavyLight,
    onPrimaryContainer = Color.White,
    secondary = ShopKartAmberLight,
    onSecondary = ShopKartNavyDark,
    background = ShopKartNavyDark,
    surface = ShopKartNavyMedium,
    onBackground = Color.White,
    onSurface = Color.White,
    error = ShopKartRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ShopKartAmber,
    onPrimary = ShopKartNavyDark,
    primaryContainer = ShopKartNavyMedium,
    onPrimaryContainer = Color.White,
    secondary = ShopKartCyan,
    onSecondary = Color.White,
    tertiary = ShopKartYellow,
    background = ShopKartBackground,
    surface = ShopKartCardSurface,
    onBackground = ShopKartTextPrimary,
    onSurface = ShopKartTextPrimary,
    error = ShopKartRed,
    onError = Color.White,
    surfaceVariant = Color(0xFFF0F2F2),
    onSurfaceVariant = ShopKartTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Keep Amazon-style clean light theme by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
