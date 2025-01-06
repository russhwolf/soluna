package com.russhwolf.soluna.mobile.graphics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.russhwolf.soluna.AstronomicalCalculator
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.riseOrNull
import com.russhwolf.soluna.setOrNull
import com.russhwolf.soluna.time.InstantAstronomicalCalculator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

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
    currentTimeFlow: StateFlow<Instant>,
    timeZone: TimeZone,
    latitude: Double,
    longitude: Double,
    mode: SunMoonTimesGraphicState.Mode,
    calculatorFactory: (Int, Month, Int) -> AstronomicalCalculator<Instant> =
        InstantAstronomicalCalculator.factory(timeZone, latitude, longitude)
): SunMoonTimesGraphicState {
    val currentTimeState = currentTimeFlow.collectAsState()
    val currentTime = currentTimeState.value

    return when (mode) {
        SunMoonTimesGraphicState.Mode.Next -> {
            val currentLocalDateTime = currentTime.toLocalDateTime(timeZone)
            val dateToday = currentLocalDateTime.date
            val dateTomorrow = dateToday.plus(1, DateTimeUnit.Companion.DAY)
            val currentTimeTomorrow = dateTomorrow.atTime(currentLocalDateTime.time).toInstant(timeZone)

            val calculatorToday = calculatorFactory(dateToday.year, dateToday.month, dateToday.dayOfMonth)
            val calculatorTomorrow = calculatorFactory(dateTomorrow.year, dateTomorrow.month, dateTomorrow.dayOfMonth)

            val sunTimesToday = calculatorToday.sunTimes
            val sunTimesTomorrow = calculatorTomorrow.sunTimes
            val moonTimesToday = calculatorToday.moonTimes
            val moonTimesTomorrow = calculatorTomorrow.moonTimes

            val sunTimes = buildNextTimes(currentTime, currentTimeTomorrow, sunTimesToday, sunTimesTomorrow)
            val moonTimes = buildNextTimes(currentTime, currentTimeTomorrow, moonTimesToday, moonTimesTomorrow)

            SunMoonTimesGraphicState(
                currentTime = currentTime,
                sunTimes = sunTimes,
                moonTimes = moonTimes,
                timeZone = timeZone,
                mode = mode
            )
        }

        SunMoonTimesGraphicState.Mode.Daily -> {

            val date = currentTime.toLocalDateTime(timeZone).date
            val calculator = calculatorFactory(date.year, date.month, date.dayOfMonth)

            SunMoonTimesGraphicState(
                currentTime = currentTime,
                sunTimes = calculator.sunTimes,
                moonTimes = calculator.moonTimes,
                timeZone = timeZone,
                mode = mode
            )
        }
    }
}


private fun <TimeUnit : Comparable<TimeUnit>> RiseSetResult<TimeUnit>.cutoffBefore(cutoffTime: TimeUnit): RiseSetResult<TimeUnit> =
    when (this) {
        is RiseSetResult.RiseThenSet -> if (setTime < cutoffTime) RiseSetResult.DownAllDay else if (riseTime < cutoffTime) RiseSetResult.SetOnly(
            setTime
        ) else this

        is RiseSetResult.SetThenRise -> if (riseTime < cutoffTime) RiseSetResult.UpAllDay else if (setTime < cutoffTime) RiseSetResult.RiseOnly(
            setTime
        ) else this

        is RiseSetResult.RiseOnly -> if (riseTime < cutoffTime) RiseSetResult.UpAllDay else this
        is RiseSetResult.SetOnly -> if (setTime < cutoffTime) RiseSetResult.DownAllDay else this
        RiseSetResult.UpAllDay,
        RiseSetResult.DownAllDay,
        RiseSetResult.Unknown -> this
    }

private fun <TimeUnit : Comparable<TimeUnit>> RiseSetResult<TimeUnit>.cutoffAfter(cutoffTime: TimeUnit): RiseSetResult<TimeUnit> =
    when (this) {
        is RiseSetResult.RiseThenSet -> if (riseTime > cutoffTime) RiseSetResult.DownAllDay else if (setTime > cutoffTime) RiseSetResult.RiseOnly(
            riseTime
        ) else this

        is RiseSetResult.SetThenRise -> if (setTime > cutoffTime) RiseSetResult.UpAllDay else if (riseTime > cutoffTime) RiseSetResult.SetOnly(
            setTime
        ) else this

        is RiseSetResult.RiseOnly -> if (riseTime > cutoffTime) RiseSetResult.UpAllDay else this
        is RiseSetResult.SetOnly -> if (setTime > cutoffTime) RiseSetResult.DownAllDay else this
        RiseSetResult.UpAllDay,
        RiseSetResult.DownAllDay,
        RiseSetResult.Unknown -> this
    }

private fun <TimeUnit> RiseSetResult<TimeUnit>.appendTimes(times: RiseSetResult<TimeUnit>): RiseSetResult<TimeUnit> where TimeUnit : Any, TimeUnit : Comparable<TimeUnit> =
    when (this) {
        is RiseSetResult.RiseThenSet,
        is RiseSetResult.SetThenRise -> this

        is RiseSetResult.RiseOnly -> times.setOrNull?.let { RiseSetResult.RiseThenSet(riseTime, it) } ?: this
        is RiseSetResult.SetOnly -> times.riseOrNull?.let { RiseSetResult.SetThenRise(setTime, it) } ?: this
        RiseSetResult.UpAllDay,
        RiseSetResult.DownAllDay,
        RiseSetResult.Unknown -> times
    }

private fun <TimeUnit : Comparable<TimeUnit>> buildNextTimes(
    startTime: TimeUnit,
    endTime: TimeUnit,
    timesToday: RiseSetResult<TimeUnit>,
    timesTomorrow: RiseSetResult<TimeUnit>
): RiseSetResult<TimeUnit> = timesToday.cutoffBefore(startTime).appendTimes(timesTomorrow.cutoffAfter(endTime))
