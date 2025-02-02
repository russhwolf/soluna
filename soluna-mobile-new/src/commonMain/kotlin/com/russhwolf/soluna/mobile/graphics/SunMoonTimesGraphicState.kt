package com.russhwolf.soluna.mobile.graphics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.mobile.repository.AstronomicalTimesRepository
import com.russhwolf.soluna.mobile.repository.CurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.SelectableLocation
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds

data class SunMoonTimesGraphicState(
    val currentTime: Instant,
    val sunTimes: RiseSetResult<Instant>,
    val moonTimes: RiseSetResult<Instant>,
    val timeZone: TimeZone,
    val mode: Mode = Mode.Daily
) {
    enum class Mode {
        Daily, Next
    }
}

@Composable
fun sunMoonTimesGraphicState(
    location: SelectableLocation,
    currentTimeRepository: CurrentTimeRepository,
    astronomicalTimesRepository: AstronomicalTimesRepository,
    mode: SunMoonTimesGraphicState.Mode
): SunMoonTimesGraphicState {
    val timeZone = TimeZone.of(location.timeZone)

    return when (mode) {
        SunMoonTimesGraphicState.Mode.Next -> {
            val astronomicalData =
                astronomicalTimesRepository.getUpcomingTimes(location, 1.seconds).collectAsState().value
            val currentTime = currentTimeRepository.getCurrentTime()

            SunMoonTimesGraphicState(
                currentTime = currentTime,
                sunTimes = astronomicalData.sunTimes,
                moonTimes = astronomicalData.moonTimes,
                timeZone = timeZone,
                mode = mode
            )
        }

        SunMoonTimesGraphicState.Mode.Daily -> {
            val currentTime = currentTimeRepository.getCurrentTime()
            val date = currentTime.toLocalDateTime(timeZone).date
            val astronomicalData = astronomicalTimesRepository.getTimesForDate(date, location)

            SunMoonTimesGraphicState(
                currentTime = currentTime,
                sunTimes = astronomicalData.sunTimes,
                moonTimes = astronomicalData.moonTimes,
                timeZone = timeZone,
                mode = mode
            )
        }
    }
}
