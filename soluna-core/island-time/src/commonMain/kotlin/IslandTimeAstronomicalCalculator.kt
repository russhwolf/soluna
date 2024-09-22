package com.russhwolf.soluna.time

import com.russhwolf.soluna.AstronomicalCalculator
import com.russhwolf.soluna.MillisTimeAstronomicalCalculator
import com.russhwolf.soluna.map
import io.islandtime.Date
import io.islandtime.Instant
import io.islandtime.Month
import io.islandtime.Time
import io.islandtime.TimeZone
import io.islandtime.at
import io.islandtime.atTime
import io.islandtime.measures.milliseconds
import io.islandtime.toDateTimeAt

class TimeAstronomicalCalculator(
    date: Date,
    zone: TimeZone,
    latitude: Double, // Degrees
    longitude: Double // Degrees
) : AstronomicalCalculator<Time> by MillisTimeAstronomicalCalculator(
    date.year,
    date.monthNumber,
    date.dayOfMonth,
    offsetHoursAtNoon(date, zone),
    latitude,
    longitude
).map({ Instant(it.milliseconds).toDateTimeAt(zone).time }) {
    companion object {
        fun factory(timeZone: TimeZone, latitude: Double, longitude: Double) = { year: Int, month: Month, day: Int ->
            TimeAstronomicalCalculator(Date(year, month, day), timeZone, latitude, longitude)
        }
    }
}

private fun offsetHoursAtNoon(date: Date, zone: TimeZone) =
    date.atTime(12, 0).at(zone).offset.totalSeconds.toLong() / 3600.0
