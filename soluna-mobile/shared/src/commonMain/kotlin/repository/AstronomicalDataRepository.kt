package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.map
import com.russhwolf.soluna.time.LocalTimeAstronomicalCalculator
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

interface AstronomicalDataRepository {

    fun getTimes(date: LocalDate, zone: TimeZone, latitude: Double, longitude: Double): AstronomicalData

    class Impl : AstronomicalDataRepository {

        override fun getTimes(date: LocalDate, zone: TimeZone, latitude: Double, longitude: Double): AstronomicalData {
            val calculator = LocalTimeAstronomicalCalculator(date, zone, latitude, longitude)
                .map { time -> LocalDateTime(date, time).toInstant(zone) }
            val (sunriseTime, sunsetTime) = calculator.sunTimes.toPair()
            val (moonriseTime, moonsetTime) = calculator.moonTimes.toPair()
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

fun <T : Any> RiseSetResult<T>.toPair(): Pair<T?, T?> = when (this) {
    is RiseSetResult.RiseThenSet -> riseTime to setTime
    is RiseSetResult.SetThenRise -> riseTime to setTime
    is RiseSetResult.RiseOnly -> riseTime to null
    is RiseSetResult.SetOnly -> null to setTime
    RiseSetResult.UpAllDay,
    RiseSetResult.DownAllDay,
    RiseSetResult.Unknown -> null to null
}
