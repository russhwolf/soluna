package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

interface UpcomingTimesRepository {

    fun getUpcomingTimes(): Flow<UpcomingTimes?>

    class Impl(
        private val locationRepository: LocationRepository,
        private val astronomicalDataRepository: AstronomicalDataRepository,
        private val clock: Clock
    ) : UpcomingTimesRepository {
        override fun getUpcomingTimes(): Flow<UpcomingTimes?> =
            locationRepository.getSelectedLocation().map(::getUpcomingTimesForLocation)

        private fun getUpcomingTimesForLocation(location: Location?): UpcomingTimes? {
            location ?: return null

            val zone = TimeZone.of(location.timeZone)
            val currentInstant = clock.now()
            val today = currentInstant.toLocalDateTime(zone).date
            val tomorrow = today.plus(DateTimeUnit.DateBased.DayBased(1))
            val times =
                astronomicalDataRepository.getTimes(today, zone, location.latitude, location.longitude)
            val timesTomorrow =
                astronomicalDataRepository.getTimes(tomorrow, zone, location.latitude, location.longitude)

            return UpcomingTimes(
                sunriseTime = times.sunriseTime?.takeIf { it > currentInstant } ?: timesTomorrow.sunriseTime,
                sunsetTime = times.sunsetTime?.takeIf { it > currentInstant } ?: timesTomorrow.sunsetTime,
                moonriseTime = times.moonriseTime?.takeIf { it > currentInstant } ?: timesTomorrow.moonriseTime,
                moonsetTime = times.moonsetTime?.takeIf { it > currentInstant } ?: timesTomorrow.moonsetTime
            )
        }
    }
}

data class UpcomingTimes(
    val sunriseTime: Instant?,
    val sunsetTime: Instant?,
    val moonriseTime: Instant?,
    val moonsetTime: Instant?
)
