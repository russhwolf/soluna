package com.russhwolf.soluna.calendar

import io.islandtime.Month
import io.islandtime.TimeZone

fun main(vararg args: String) {
    val locationName = args[0]
    val year = args[1].toInt()
    val latitude = args[2].toDouble()
    val longitude = args[3].toDouble()
    val timeZone = TimeZone(args[4])
    renderCalendars(locationName, year, latitude, longitude, timeZone)
}

private fun renderCalendars(
    locationName: String,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone
) {
    for (month in Month.values().drop(0).take(1)) {
        renderComposeCalendarToFile(locationName, month, year, latitude, longitude, timeZone)
    }
}
