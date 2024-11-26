package com.russhwolf.soluna.mobile.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun SolunaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
    val colorScheme = getMaterialColorScheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        lightScheme = lightScheme,
        darkScheme = darkScheme,
    )

    val extendedColorScheme = if (darkTheme) extendedDark else extendedLight

    CompositionLocalProvider(
        LocalSolunaColorScheme provides SolunaColorScheme(colorScheme, extendedColorScheme)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

object SolunaTheme {
    /**
     * Retrieves the current [ColorScheme] at the call site's position in the hierarchy.
     */
    val colorScheme: SolunaColorScheme
        @Composable @ReadOnlyComposable get() = LocalSolunaColorScheme.current

    /**
     * Retrieves the current [Typography] at the call site's position in the hierarchy.
     */
    val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    /**
     * Retrieves the current [Shapes] at the call site's position in the hierarchy.
     */
    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes
}

val LocalSolunaColorScheme = staticCompositionLocalOf { SolunaColorScheme() }

@Composable
expect fun getMaterialColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    lightScheme: ColorScheme,
    darkScheme: ColorScheme
): ColorScheme
