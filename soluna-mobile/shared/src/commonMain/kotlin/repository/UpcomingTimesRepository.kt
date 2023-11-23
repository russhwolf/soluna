package com.russhwolf.soluna.mobile.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

interface UpcomingTimesRepository {

    fun getUpcomingTimes(location: SelectableLocation): Flow<UpcomingTimes?>

    class Impl(
        private val astronomicalDataRepository: AstronomicalDataRepository,
        private val currentTimeRepository: CurrentTimeRepository
    ) : UpcomingTimesRepository {
        override fun getUpcomingTimes(location: SelectableLocation): Flow<UpcomingTimes?> =
            currentTimeRepository.getCurrentTimeFlow(1.minutes)
                .map { getUpcomingTimesForLocation(location, it) }

        private fun getUpcomingTimesForLocation(
            location: SelectableLocation?,
            currentInstant: Instant
        ): UpcomingTimes? {
            location ?: return null

            val zone = TimeZone.of(location.timeZone)
            val today = currentInstant.toLocalDateTime(zone).date
            val tomorrow = today.plus(DateTimeUnit.DayBased(1))
            val times =
                astronomicalDataRepository.getTimes(today, zone, location.latitude, location.longitude)
            val timesTomorrow =
                astronomicalDataRepository.getTimes(tomorrow, zone, location.latitude, location.longitude)

            return UpcomingTimes(
                sunriseTime = times.sunriseTime?.takeIf { it > currentInstant }
                    ?: timesTomorrow.sunriseTime?.takeIf { it < currentInstant.plus(1.days) },
                sunsetTime = times.sunsetTime?.takeIf { it > currentInstant }
                    ?: timesTomorrow.sunsetTime?.takeIf { it < currentInstant.plus(1.days) },
                moonriseTime = times.moonriseTime?.takeIf { it > currentInstant }
                    ?: timesTomorrow.moonriseTime?.takeIf { it < currentInstant.plus(1.days) },
                moonsetTime = times.moonsetTime?.takeIf { it > currentInstant }
                    ?: timesTomorrow.moonsetTime?.takeIf { it < currentInstant.plus(1.days) }
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
