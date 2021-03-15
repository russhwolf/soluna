package com.russhwolf.soluna.time

import com.russhwolf.soluna.MoonPhase
import io.islandtime.Date
import io.islandtime.TimeZone
import io.islandtime.ZonedDateTime
import io.islandtime.at
import io.islandtime.atTime
import com.russhwolf.soluna.moonPhase as coreMoonPhase
import com.russhwolf.soluna.moonTimes as coreMoonTimes
import com.russhwolf.soluna.sunTimes as coreSunTimes

fun sunTimes(
    date: Date,
    zone: TimeZone,
    latitude: Double, // Degrees
    longitude: Double // Degrees
): Pair<ZonedDateTime?, ZonedDateTime?> {
    val (riseMillis, setMillis) = coreSunTimes(
        year = date.year,
        month = date.monthNumber,
        day = date.dayOfMonth,
        offset = offsetHoursAtNoon(date, zone),
        latitude = latitude,
        longitude = longitude
    )
    return riseMillis.toZonedDateTime(zone) to setMillis.toZonedDateTime(zone)
}

fun moonTimes(
    date: Date,
    zone: TimeZone,
    latitude: Double, // Degrees
    longitude: Double // Degrees
): Pair<ZonedDateTime?, ZonedDateTime?> {
    val (riseMillis, setMillis) = coreMoonTimes(
        year = date.year,
        month = date.monthNumber,
        day = date.dayOfMonth,
        offset = offsetHoursAtNoon(date, zone),
        latitude = latitude,
        longitude = longitude
    )
    return riseMillis.toZonedDateTime(zone) to setMillis.toZonedDateTime(zone)
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

private fun Long?.toZonedDateTime(zone: TimeZone) =
    this?.let { ZonedDateTime.fromMillisecondOfUnixEpoch(it, zone) }

private fun offsetHoursAtNoon(date: Date, zone: TimeZone) =
    date.atTime(12, 0).at(zone).offset.totalSeconds.toLong() / 3600.0
