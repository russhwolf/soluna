package com.russhwolf.soluna.mobile.graphics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.mobile.repository.AstronomicalTimesRepository
import com.russhwolf.soluna.mobile.repository.CurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.SelectableLocation
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Duration.Companion.seconds

sealed interface SunMoonTimesGraphicState {
    val sunTimes: RiseSetResult<Instant>
    val moonTimes: RiseSetResult<Instant>
    val timeZone: TimeZone

    data class Daily(
        val date: LocalDate,
        override val sunTimes: RiseSetResult<Instant>,
        override val moonTimes: RiseSetResult<Instant>,
        override val timeZone: TimeZone
    ) : SunMoonTimesGraphicState

    data class Next(
        val currentTime: Instant,
        override val sunTimes: RiseSetResult<Instant>,
        override val moonTimes: RiseSetResult<Instant>,
        override val timeZone: TimeZone
    ) : SunMoonTimesGraphicState
}

@Composable
fun sunMoonTimesGraphicStateNext(
    location: SelectableLocation,
    currentTimeRepository: CurrentTimeRepository,
    astronomicalTimesRepository: AstronomicalTimesRepository,
): SunMoonTimesGraphicState.Next {
    val timeZone = TimeZone.of(location.timeZone)
    val astronomicalData =
        astronomicalTimesRepository.getUpcomingTimes(location, 1.seconds).collectAsState().value
    val currentTime = currentTimeRepository.getCurrentTime()

    return SunMoonTimesGraphicState.Next(
        currentTime = currentTime,
        sunTimes = astronomicalData.sunTimes,
        moonTimes = astronomicalData.moonTimes,
        timeZone = timeZone
    )
}

@Composable
fun sunMoonTimesGraphicStateDaily(
    location: SelectableLocation,
    astronomicalTimesRepository: AstronomicalTimesRepository,
    date: LocalDate
): SunMoonTimesGraphicState.Daily {
    val timeZone = TimeZone.of(location.timeZone)
    val astronomicalData = astronomicalTimesRepository.getTimesForDate(date, location)

    return SunMoonTimesGraphicState.Daily(
        date = date,
        sunTimes = astronomicalData.sunTimes,
        moonTimes = astronomicalData.moonTimes,
        timeZone = timeZone
    )
}

