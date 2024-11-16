package com.russhwolf.soluna.time

import com.russhwolf.soluna.AstronomicalCalculator
import com.russhwolf.soluna.MillisTimeAstronomicalCalculator
import com.russhwolf.soluna.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class LocalTimeAstronomicalCalculator(
    date: LocalDate,
    zone: TimeZone,
    latitude: Double, // Degrees
    longitude: Double // Degrees
) : AstronomicalCalculator<LocalTime> by MillisTimeAstronomicalCalculator(
    date.year,
    date.monthNumber,
    date.dayOfMonth,
    offsetHoursAtNoon(date, zone),
    latitude,
    longitude
).map({ Instant.fromEpochMilliseconds(it).toLocalDateTime(zone).time }) {
    companion object {
        fun factory(timeZone: TimeZone, latitude: Double, longitude: Double) = { year: Int, month: Month, day: Int ->
            LocalTimeAstronomicalCalculator(LocalDate(year, month, day), timeZone, latitude, longitude)
        }
    }
}

class InstantAstronomicalCalculator(
    date: LocalDate,
    zone: TimeZone,
    latitude: Double, // Degrees
    longitude: Double // Degrees
) : AstronomicalCalculator<Instant> by MillisTimeAstronomicalCalculator(
    date.year,
    date.monthNumber,
    date.dayOfMonth,
    offsetHoursAtNoon(date, zone),
    latitude,
    longitude
).map({ Instant.fromEpochMilliseconds(it) }) {
    companion object {
        fun factory(timeZone: TimeZone, latitude: Double, longitude: Double) = { year: Int, month: Month, day: Int ->
            InstantAstronomicalCalculator(LocalDate(year, month, day), timeZone, latitude, longitude)
        }
    }
}

private fun offsetHoursAtNoon(date: LocalDate, zone: TimeZone) =
    zone.offsetAt(date.atTime(12, 0).toInstant(zone)).totalSeconds / 3600.0
