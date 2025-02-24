package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.AstronomicalCalculator
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.riseOrNull
import com.russhwolf.soluna.setOrNull
import com.russhwolf.soluna.time.InstantAstronomicalCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class AstronomicalTimesRepository(
    private val currentTimeRepository: CurrentTimeRepository,
    // TODO make this less terrible
    private val calculatorFactoryBuilder: (TimeZone, Double, Double) -> (Int, Month, Int) -> AstronomicalCalculator<Instant> =
        InstantAstronomicalCalculator::factory,
    private val context: CoroutineContext = EmptyCoroutineContext,
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + context)

    fun getUpcomingTimes(location: SelectableLocation, period: Duration = 1.seconds): StateFlow<AstronomicalTimes> =
        currentTimeRepository.getCurrentTimeFlow(period)
            .map { computeUpcomingTimes(it, location) }
            .stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(),
                computeUpcomingTimes(currentTimeRepository.getCurrentTime(), location)
            )

    fun getTimesForDate(
        date: LocalDate,
        location: SelectableLocation
    ): AstronomicalTimes {
        val astronomicalCalculator = calculatorFactoryBuilder(
            location.timeZone,
            location.latitude,
            location.longitude
        ).invoke(
            date.year,
            date.month,
            date.dayOfMonth
        )
        return AstronomicalTimes(astronomicalCalculator.sunTimes, astronomicalCalculator.moonTimes)
    }

    private fun computeUpcomingTimes(
        currentTime: Instant,
        selectedLocation: SelectableLocation
    ): AstronomicalTimes {
        val localDateTime = currentTime.toLocalDateTime(selectedLocation.timeZone)

        val dateToday = localDateTime.date
        val dateTomorrow = dateToday.plus(1, DateTimeUnit.DAY)
        val currentTimeTomorrow = dateTomorrow.atTime(localDateTime.time).toInstant(selectedLocation.timeZone)

        val calculatorFactory =
            calculatorFactoryBuilder.invoke(
                selectedLocation.timeZone,
                selectedLocation.latitude,
                selectedLocation.longitude
            )

        val timesToday = calculatorFactory(dateToday.year, dateToday.month, dateToday.dayOfMonth)
        val timesTomorrow = calculatorFactory(dateTomorrow.year, dateTomorrow.month, dateTomorrow.dayOfMonth)

        val sunTimesToday = timesToday.sunTimes
        val sunTimesTomorrow = timesTomorrow.sunTimes
        val moonTimesToday = timesToday.moonTimes
        val moonTimesTomorrow = timesTomorrow.moonTimes

        val sunTimes = buildNextTimes(currentTime, currentTimeTomorrow, sunTimesToday, sunTimesTomorrow)
        val moonTimes = buildNextTimes(currentTime, currentTimeTomorrow, moonTimesToday, moonTimesTomorrow)

        return AstronomicalTimes(sunTimes, moonTimes)
    }
}

data class AstronomicalTimes(
    val sunTimes: RiseSetResult<Instant>,
    val moonTimes: RiseSetResult<Instant>,
)

private fun <TimeUnit : Comparable<TimeUnit>> RiseSetResult<TimeUnit>.cutoffBefore(cutoffTime: TimeUnit): RiseSetResult<TimeUnit> =
    when (this) {
        is RiseSetResult.RiseThenSet -> when {
            setTime < cutoffTime -> RiseSetResult.DownAllDay
            riseTime < cutoffTime -> RiseSetResult.SetOnly(setTime)
            else -> this
        }

        is RiseSetResult.SetThenRise -> when {
            riseTime < cutoffTime -> RiseSetResult.UpAllDay
            setTime < cutoffTime -> RiseSetResult.RiseOnly(setTime)
            else -> this
        }

        is RiseSetResult.RiseOnly -> when {
            riseTime < cutoffTime -> RiseSetResult.UpAllDay
            else -> this
        }

        is RiseSetResult.SetOnly -> when {
            setTime < cutoffTime -> RiseSetResult.DownAllDay
            else -> this
        }

        RiseSetResult.UpAllDay,
        RiseSetResult.DownAllDay,
        RiseSetResult.Unknown -> this
    }

private fun <TimeUnit : Comparable<TimeUnit>> RiseSetResult<TimeUnit>.cutoffAfter(cutoffTime: TimeUnit): RiseSetResult<TimeUnit> =
    when (this) {
        is RiseSetResult.RiseThenSet -> when {
            riseTime > cutoffTime -> RiseSetResult.DownAllDay
            setTime > cutoffTime -> RiseSetResult.RiseOnly(riseTime)
            else -> this
        }

        is RiseSetResult.SetThenRise -> when {
            setTime > cutoffTime -> RiseSetResult.UpAllDay
            riseTime > cutoffTime -> RiseSetResult.SetOnly(setTime)
            else -> this
        }

        is RiseSetResult.RiseOnly -> when {
            riseTime > cutoffTime -> RiseSetResult.UpAllDay
            else -> this
        }

        is RiseSetResult.SetOnly -> when {
            setTime > cutoffTime -> RiseSetResult.DownAllDay
            else -> this
        }

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
): RiseSetResult<TimeUnit> =
    timesToday.cutoffBefore(startTime).appendTimes(timesTomorrow.cutoffAfter(endTime))
