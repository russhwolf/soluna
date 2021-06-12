package com.russhwolf.soluna.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple800,
    primaryVariant = Purple800Light,
    secondary = Pink900,
    secondaryVariant = Pink900Light,
    onPrimary = Color.White,
    onSecondary = Color.White
)

private val LightColorPalette = lightColors(
    primary = Yellow600,
    primaryVariant = Yellow600Light,
    secondary = Amber800,
    secondaryVariant = Amber800Light,
    onPrimary = Color.Black,
    onSecondary = Color.Black
)

@Composable
fun SolunaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

typealias SolunaTheme = MaterialTheme
