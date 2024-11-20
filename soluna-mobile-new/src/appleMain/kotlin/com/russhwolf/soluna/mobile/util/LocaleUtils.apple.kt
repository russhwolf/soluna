package com.russhwolf.soluna.mobile.util

import androidx.compose.runtime.Composable
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

internal actual object LocaleUtils {
    @Composable
    actual fun is24h(): Boolean {
        return NSDateFormatter.dateFormatFromTemplate("j", options = 0u, locale = NSLocale.currentLocale)
            ?.contains("a") == false
    }
}
