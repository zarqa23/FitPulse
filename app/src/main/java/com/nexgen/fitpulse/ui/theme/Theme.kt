package com.nexgen.fitpulse.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// Define your custom colors for the dark theme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF025D93),  // Deep blue
    secondary = Color(0xFF024974),  // Slightly darker blue
    tertiary = Color(0xFF012134),  // Very dark blue
    background = Color(0xFF011C2D),  // Almost black
    surface = Color(0xFF012033),  // Darker blue
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

// Define your custom colors for the light theme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF025D93),  // Same deep blue but may appear lighter
    secondary = Color(0xFF024974),  // Same slightly darker blue but lighter in light theme
    tertiary = Color(0xFF012134),  // Same very dark blue but will be more visible in light
    background = Color(0xFFE0F1F6),  // Lighter background color for light theme
    surface = Color(0xFFD1E3F1),  // Lighter surface color for light theme
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun FitPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,  // Ensure Typography is defined
        content = content
    )
}
