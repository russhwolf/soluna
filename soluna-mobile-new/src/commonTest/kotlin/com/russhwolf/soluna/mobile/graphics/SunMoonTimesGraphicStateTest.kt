package com.russhwolf.soluna.mobile.graphics

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.mobile.repository.AstronomicalTimesRepository
import com.russhwolf.soluna.mobile.repository.CurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.SelectableLocation
import com.russhwolf.soluna.mobile.test.TestAstronomicalCalculatorFactory
import com.russhwolf.soluna.mobile.test.TestClock
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals

class SunMoonTimesGraphicStateTest {

    val date = LocalDate(2000, Month.JANUARY, 1)
    val currentTimes = listOf(
        LocalDateTime(date, LocalTime(4, 0)).toInstant(TimeZone.of("UTC")),
        LocalDateTime(date, LocalTime(12, 0)).toInstant(TimeZone.of("UTC")),
        LocalDateTime(date, LocalTime(20, 0)).toInstant(TimeZone.of("UTC"))
    )

    val location = SelectableLocation(0L, "Test Location", 0.0, 0.0, TimeZone.of("UTC"), false)
    val clock = TestClock(currentTimes)
    val currentTimeRepository = CurrentTimeRepository(clock)

    @Test
    fun nextTimes() = runTest {
        val astronomicalTimesRepository = AstronomicalTimesRepository(
            currentTimeRepository,
            TestAstronomicalCalculatorFactory { day ->
                RiseSetResult.RiseThenSet(
                    riseTime = LocalDateTime(2000, 1, day, 8, day).toInstant(TimeZone.of("UTC")),
                    setTime = LocalDateTime(2000, 1, day, 16, day).toInstant(TimeZone.of("UTC"))
                )
            }
        )

        moleculeFlow(RecompositionMode.Immediate) {
            sunMoonTimesGraphicStateNext(
                location,
                currentTimeRepository,
                astronomicalTimesRepository,
            )
        }.test {
            val expectedTimes = arrayOf<RiseSetResult<Instant>>(
                RiseSetResult.RiseThenSet(
                    riseTime = LocalDateTime(2000, 1, 1, 8, 1).toInstant(TimeZone.of("UTC")),
                    setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.of("UTC"))
                ),
                RiseSetResult.SetThenRise(
                    setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.of("UTC")),
                    riseTime = LocalDateTime(2000, 1, 2, 8, 2).toInstant(TimeZone.of("UTC"))
                ),
                RiseSetResult.RiseThenSet(
                    riseTime = LocalDateTime(2000, 1, 2, 8, 2).toInstant(TimeZone.of("UTC")),
                    setTime = LocalDateTime(2000, 1, 2, 16, 2).toInstant(TimeZone.of("UTC"))
                )
            )

            for (i in expectedTimes.indices) {
                assertEquals(
                    expected = SunMoonTimesGraphicState.Next(
                        currentTime = currentTimes[i],
                        sunTimes = expectedTimes[i],
                        moonTimes = expectedTimes[i],
                        timeZone = TimeZone.of("UTC"),
                    ),
                    actual = awaitItem()
                )
                clock.tick()
            }
        }
    }

    @Test
    fun dailyTimes() = runTest {
        val astronomicalTimesRepository = AstronomicalTimesRepository(
            currentTimeRepository,
            TestAstronomicalCalculatorFactory { day ->
                RiseSetResult.RiseThenSet(
                    riseTime = LocalDateTime(2000, 1, day, 8, day).toInstant(TimeZone.of("UTC")),
                    setTime = LocalDateTime(2000, 1, day, 16, day).toInstant(TimeZone.of("UTC"))
                )
            }
        )

        moleculeFlow(RecompositionMode.Immediate) {
            sunMoonTimesGraphicStateDaily(
                location,
                astronomicalTimesRepository,
                date,
            )
        }.test {
            val expectedTimes = RiseSetResult.RiseThenSet(
                riseTime = LocalDateTime(2000, 1, 1, 8, 1).toInstant(TimeZone.of("UTC")),
                setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.of("UTC"))
            )

            assertEquals(
                expected = SunMoonTimesGraphicState.Daily(
                    date = date,
                    sunTimes = expectedTimes,
                    moonTimes = expectedTimes,
                    timeZone = TimeZone.of("UTC"),
                ),
                actual = awaitItem()
            )
        }

    }
}
