package com.russhwolf.soluna.time

import com.russhwolf.soluna.MoonPhase
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toInstant
import com.russhwolf.soluna.moonPhase as coreMoonPhase
import com.russhwolf.soluna.moonTimes as coreMoonTimes
import com.russhwolf.soluna.sunTimes as coreSunTimes

fun sunTimes(
    date: LocalDate,
    zone: TimeZone,
    latitude: Double, // Degrees
    longitude: Double // Degrees
): Pair<Instant?, Instant?> {
    val (riseMillis, setMillis) = coreSunTimes(
        year = date.year,
        month = date.monthNumber,
        day = date.dayOfMonth,
        offset = offsetHoursAtNoon(date, zone),
        latitude = latitude,
        longitude = longitude
    )
    return riseMillis?.let { Instant.fromEpochMilliseconds(it) } to setMillis?.let { Instant.fromEpochMilliseconds(it) }
}

fun moonTimes(
    date: LocalDate,
    zone: TimeZone,
    latitude: Double, // Degrees
    longitude: Double // Degrees
): Pair<Instant?, Instant?> {
    val (riseMillis, setMillis) = coreMoonTimes(
        year = date.year,
        month = date.monthNumber,
        day = date.dayOfMonth,
        offset = offsetHoursAtNoon(date, zone),
        latitude = latitude,
        longitude = longitude
    )
    return riseMillis?.let { Instant.fromEpochMilliseconds(it) } to setMillis?.let { Instant.fromEpochMilliseconds(it) }
}

fun moonPhase(
    date: LocalDate,
    zone: TimeZone,
    longitude: Double // Degrees
): MoonPhase? {
    return coreMoonPhase(
        year = date.year,
        month = date.monthNumber,
        day = date.dayOfMonth,
        offset = offsetHoursAtNoon(date, zone),
        longitude = longitude
    )
}

private fun offsetHoursAtNoon(date: LocalDate, zone: TimeZone) =
    zone.offsetAt(date.atTime(12, 0).toInstant(zone)).totalSeconds / 3600.0
