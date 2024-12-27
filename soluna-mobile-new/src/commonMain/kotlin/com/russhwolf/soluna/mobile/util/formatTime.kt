package com.russhwolf.soluna.mobile.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun LocalTime.formatTime(): String {
    val is24h = LocaleUtils.is24h()
    val timeFormat = remember(is24h) {
        if (is24h) {
            LocalTime.Companion.Format {
                hour(padding = Padding.NONE)
                char(':')
                minute(padding = Padding.ZERO)
            }
        } else {
            LocalTime.Companion.Format {
                amPmHour(padding = Padding.NONE)
                char(':')
                minute(padding = Padding.ZERO)
                char(' ')
                amPmMarker("AM", "PM") // TODO this should be localized
            }
        }
    }
    return format(timeFormat)
}


@Composable
internal fun Instant.formatTime(timeZone: TimeZone): String =
    toLocalDateTime(timeZone).time.formatTime()
