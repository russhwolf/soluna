package com.russhwolf.soluna.mobile.util

import androidx.compose.runtime.Composable

internal expect object LocaleUtils {
    @Composable
    fun is24h(): Boolean
}
