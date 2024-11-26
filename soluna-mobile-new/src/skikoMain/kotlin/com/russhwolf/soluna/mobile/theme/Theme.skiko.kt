package com.russhwolf.soluna.mobile.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable


@Composable
actual fun getMaterialColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    lightScheme: ColorScheme,
    darkScheme: ColorScheme
) = when {
    darkTheme -> darkScheme
    else -> lightScheme
}
