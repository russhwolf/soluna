package com.russhwolf.soluna.mobile.util

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

internal actual object LocaleUtils {
    @Composable
    actual fun is24h(): Boolean {
        return DateFormat.is24HourFormat(LocalContext.current)
    }
}
