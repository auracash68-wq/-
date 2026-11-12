package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HisabKhataColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = CardWhite,
    primaryContainer = HeaderNavy,
    onPrimaryContainer = CardWhite,
    secondary = BrightBlue,
    onSecondary = CardWhite,
    tertiary = SaffronOrange,
    background = AppBackground,
    onBackground = PrimaryText,
    surface = CardWhite,
    onSurface = PrimaryText,
    surfaceVariant = AppBackground,
    onSurfaceVariant = SecondaryText,
    error = DangerRed,
    onError = CardWhite
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HisabKhataColorScheme,
        typography = Typography,
        content = content
    )
}
