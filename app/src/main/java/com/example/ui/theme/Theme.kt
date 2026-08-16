package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val KavachColorScheme =
  darkColorScheme(
    primary = KavachCyanPrimary,
    onPrimary = KavachDarkBg,
    primaryContainer = KavachCyanHover,
    onPrimaryContainer = KavachTextPrimary,
    secondary = KavachSafeGreen,
    onSecondary = KavachDarkBg,
    secondaryContainer = KavachSafeGreenBg,
    onSecondaryContainer = KavachTextPrimary,
    tertiary = KavachWarningAmber,
    error = KavachEmergencyRed,
    errorContainer = KavachEmergencyBg,
    onError = KavachTextPrimary,
    onErrorContainer = KavachEmergencyRed,
    background = KavachDarkBg,
    onBackground = KavachTextPrimary,
    surface = KavachDarkSurface,
    onSurface = KavachTextPrimary,
    surfaceVariant = KavachDarkSurfaceVariant,
    onSurfaceVariant = KavachTextSecondary,
    outline = KavachDarkCardBorder,
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = KavachColorScheme,
    typography = Typography,
    content = content
  )
}

