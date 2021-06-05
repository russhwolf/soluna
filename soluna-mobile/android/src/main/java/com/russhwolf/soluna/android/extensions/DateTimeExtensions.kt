package com.russhwolf.soluna.android.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Instant.toDisplayTime(timeZone: TimeZone): String {
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return formatter.format(toLocalDateTime(timeZone).toJavaLocalDateTime())
}
