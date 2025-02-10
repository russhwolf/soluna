package com.russhwolf.soluna.mobile.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format

fun LocalDate.formatDate(): String {
    return format(formatter)
}

// TODO read a more natural format from device
val formatter = LocalDate.Formats.ISO
