package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.time.moonTimes
import com.russhwolf.soluna.time.sunTimes
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

interface AstronomicalDataRepository {

    fun getTimes(date: LocalDate, zone: TimeZone, latitude: Double, longitude: Double): AstronomicalData

    class Impl : AstronomicalDataRepository {

        override fun getTimes(date: LocalDate, zone: TimeZone, latitude: Double, longitude: Double): AstronomicalData {
            val (sunriseTime, sunsetTime) = sunTimes(date, zone, latitude, longitude)
            val (moonriseTime, moonsetTime) = moonTimes(date, zone, latitude, longitude)
            return AstronomicalData(sunriseTime, sunsetTime, moonriseTime, moonsetTime)
        }
    }

}

data class AstronomicalData(
    val sunriseTime: Instant?,
    val sunsetTime: Instant?,
    val moonriseTime: Instant?,
    val moonsetTime: Instant?
)
