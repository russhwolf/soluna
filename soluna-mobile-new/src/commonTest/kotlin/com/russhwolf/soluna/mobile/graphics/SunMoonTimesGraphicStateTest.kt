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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals

class SunMoonTimesGraphicStateTest {
    @Test
    fun nextTimes() = runTest {
        val currentTimes = listOf(
            LocalDateTime(2000, 1, 1, 4, 0).toInstant(TimeZone.Companion.UTC),
            LocalDateTime(2000, 1, 1, 12, 0).toInstant(TimeZone.Companion.UTC),
            LocalDateTime(2000, 1, 1, 20, 0).toInstant(TimeZone.Companion.UTC)
        )

        val location = SelectableLocation(0L, "Test Location", 0.0, 0.0, "UTC", false)
        val clock = TestClock(currentTimes)
        val currentTimeRepository = CurrentTimeRepository(clock)
        val astronomicalTimesRepository = AstronomicalTimesRepository(
            currentTimeRepository,
            TestAstronomicalCalculatorFactory { day ->
                RiseSetResult.RiseThenSet(
                    riseTime = LocalDateTime(2000, 1, day, 8, day).toInstant(TimeZone.Companion.UTC),
                    setTime = LocalDateTime(2000, 1, day, 16, day).toInstant(TimeZone.Companion.UTC)
                )
            },
            StandardTestDispatcher(testScheduler),
        )

        moleculeFlow(RecompositionMode.Immediate) {
            sunMoonTimesGraphicState(
                location,
                currentTimeRepository,
                astronomicalTimesRepository,
                SunMoonTimesGraphicState.Mode.Next,
            )
        }.test {
            val expectedTimes = arrayOf<RiseSetResult<Instant>>(
                RiseSetResult.RiseThenSet(
                    riseTime = LocalDateTime(2000, 1, 1, 8, 1).toInstant(TimeZone.Companion.UTC),
                    setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.Companion.UTC)
                ),
                RiseSetResult.SetThenRise(
                    setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.Companion.UTC),
                    riseTime = LocalDateTime(2000, 1, 2, 8, 2).toInstant(TimeZone.Companion.UTC)
                ),
                RiseSetResult.RiseThenSet(
                    riseTime = LocalDateTime(2000, 1, 2, 8, 2).toInstant(TimeZone.Companion.UTC),
                    setTime = LocalDateTime(2000, 1, 2, 16, 2).toInstant(TimeZone.Companion.UTC)
                )
            )

            for (i in expectedTimes.indices) {
                assertEquals(
                    expected = SunMoonTimesGraphicState(
                        currentTime = currentTimes[i],
                        sunTimes = expectedTimes[i],
                        moonTimes = expectedTimes[i],
                        timeZone = TimeZone.of("UTC"), // Note: this is different from TimeZone.UTC
                        mode = SunMoonTimesGraphicState.Mode.Next
                    ),
                    actual = awaitItem()
                )
                clock.tick()
            }
        }
    }
}
