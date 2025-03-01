package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.mobile.test.TestAstronomicalCalculatorFactory
import com.russhwolf.soluna.mobile.test.TestClock
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class AstronomicalTimesRepositoryTest {
    val currentTimes = listOf(
        LocalDateTime(2000, 1, 1, 4, 0).toInstant(TimeZone.UTC),
        LocalDateTime(2000, 1, 1, 12, 0).toInstant(TimeZone.UTC),
        LocalDateTime(2000, 1, 1, 20, 0).toInstant(TimeZone.UTC),
        LocalDateTime(2000, 1, 2, 4, 0).toInstant(TimeZone.UTC)
    )

    val location = SelectableLocation(0L, "Test Location", 0.0, 0.0, TimeZone.UTC, false)
    val clock = TestClock(currentTimes)
    val currentTimeRepository = CurrentTimeRepository(clock)

    val calculatorFactoryBuilder = TestAstronomicalCalculatorFactory { day ->
        RiseSetResult.RiseThenSet(
            riseTime = LocalDateTime(2000, 1, day, 8, day).toInstant(TimeZone.UTC),
            setTime = LocalDateTime(2000, 1, day, 16, day).toInstant(TimeZone.UTC)
        )
    }

    @Test
    fun upcomingTimes() = runTest {
        val astronomicalTimesRepository = AstronomicalTimesRepository(
            currentTimeRepository,
            calculatorFactoryBuilder
        )

        astronomicalTimesRepository.getUpcomingTimes(backgroundScope, location).test {
            val expectedTimes = arrayOf<RiseSetResult<Instant>>(
                RiseSetResult.RiseThenSet(
                    riseTime = LocalDateTime(2000, 1, 1, 8, 1).toInstant(TimeZone.UTC),
                    setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.UTC)
                ),
                RiseSetResult.SetThenRise(
                    setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.UTC),
                    riseTime = LocalDateTime(2000, 1, 2, 8, 2).toInstant(TimeZone.UTC)
                ),
                RiseSetResult.RiseThenSet(
                    riseTime = LocalDateTime(2000, 1, 2, 8, 2).toInstant(TimeZone.UTC),
                    setTime = LocalDateTime(2000, 1, 2, 16, 2).toInstant(TimeZone.UTC)
                )
                // No 4th emission because nothing changes
            )

            for (i in expectedTimes.indices) {
                assertEquals(
                    expected = AstronomicalTimes(
                        sunTimes = expectedTimes[i],
                        moonTimes = expectedTimes[i]
                    ),
                    actual = awaitItem()
                )
                clock.tick()
            }
        }
    }

    @Test
    fun timesForDate() {
        val astronomicalTimesRepository = AstronomicalTimesRepository(currentTimeRepository, calculatorFactoryBuilder)

        val dates = currentTimes.map { it.toLocalDateTime(TimeZone.UTC).date }
        val expectedTimes = arrayOf<RiseSetResult<Instant>>(
            RiseSetResult.RiseThenSet(
                riseTime = LocalDateTime(2000, 1, 1, 8, 1).toInstant(TimeZone.UTC),
                setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.UTC)
            ),
            RiseSetResult.RiseThenSet(
                riseTime = LocalDateTime(2000, 1, 1, 8, 1).toInstant(TimeZone.UTC),
                setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.UTC)
            ),
            RiseSetResult.RiseThenSet(
                riseTime = LocalDateTime(2000, 1, 1, 8, 1).toInstant(TimeZone.UTC),
                setTime = LocalDateTime(2000, 1, 1, 16, 1).toInstant(TimeZone.UTC)
            ),
            RiseSetResult.RiseThenSet(
                riseTime = LocalDateTime(2000, 1, 2, 8, 2).toInstant(TimeZone.UTC),
                setTime = LocalDateTime(2000, 1, 2, 16, 2).toInstant(TimeZone.UTC)
            )
        )
        for (i in expectedTimes.indices) {
            assertEquals(
                expected = AstronomicalTimes(
                    sunTimes = expectedTimes[i],
                    moonTimes = expectedTimes[i]
                ),
                actual = astronomicalTimesRepository.getTimesForDate(dates[i], location)
            )
            clock.tick()
        }
    }
}
