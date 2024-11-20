package com.russhwolf.soluna.mobile.util

import androidx.compose.runtime.Composable

internal actual object LocaleUtils {
    @Composable
    actual fun is24h(): Boolean {
        // TODO (but we don't really care about JVM, just using it for previews)
        return true
    }
}
