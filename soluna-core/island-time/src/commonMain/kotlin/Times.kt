package com.russhwolf.soluna.time

import com.russhwolf.soluna.MoonPhase
import io.islandtime.Date
import io.islandtime.Instant
import io.islandtime.TimeZone
import io.islandtime.at
import io.islandtime.atTime
import io.islandtime.measures.milliseconds
import com.russhwolf.soluna.moonPhase as coreMoonPhase
import com.russhwolf.soluna.moonTimes as coreMoonTimes
import com.russhwolf.soluna.sunTimes as coreSunTimes

fun sunTimes(
    date: Date,
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
    return riseMillis?.let { Instant(it.milliseconds) } to setMillis?.let { Instant(it.milliseconds) }
}

fun moonTimes(
    date: Date,
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
    return riseMillis?.let { Instant(it.milliseconds) } to setMillis?.let { Instant(it.milliseconds) }
}

fun moonPhase(
    date: Date,
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

private fun offsetHoursAtNoon(date: Date, zone: TimeZone) =
    date.atTime(12, 0).at(zone).offset.totalSeconds.toLong() / 3600.0
