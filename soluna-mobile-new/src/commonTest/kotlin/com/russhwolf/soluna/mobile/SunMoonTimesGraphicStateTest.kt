package com.russhwolf.soluna.mobile

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.russhwolf.soluna.AstronomicalCalculator
import com.russhwolf.soluna.MoonPhase
import com.russhwolf.soluna.RiseSetResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals

class SunMoonTimesGraphicStateTest {
    @Test
    fun nextTimes() = runTest {
        val currentTimes = listOf(
            LocalDateTime(2000, 1, 1, 4, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 1, 12, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 1, 20, 0).toInstant(TimeZone.UTC)
        )

        val currentTimesFlow = currentTimes.asFlow().drop(1).stateIn(this, SharingStarted.Lazily, currentTimes.first())

        moleculeFlow(RecompositionMode.Immediate) {
            sunMoonTimesGraphicState(
                currentTimesFlow,
                TimeZone.UTC,
                0.0,
                0.0,
                SunMoonTimesGraphicState.Mode.Next,
                TestAstronomicalCalculatorFactory { day ->
                    RiseSetResult.RiseThenSet(
                        riseTime = LocalDateTime(2000, 1, day, 8, day).toInstant(TimeZone.UTC),
                        setTime = LocalDateTime(2000, 1, day, 16, day).toInstant(TimeZone.UTC)
                    )
                }
            )
        }.test {
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
            )

            for (i in expectedTimes.indices) {
                assertEquals(
                    expected = SunMoonTimesGraphicState(
                        currentTime = currentTimes[i],
                        sunTimes = expectedTimes[i],
                        moonTimes = expectedTimes[i],
                        timeZone = TimeZone.UTC,
                        mode = SunMoonTimesGraphicState.Mode.Next
                    ),
                    actual = awaitItem()
                )
            }
        }
    }
}

class TestAstronomicalCalculatorFactory(private val resultBuilder: (Int) -> RiseSetResult<Instant>) :
        (Int, Month, Int) -> AstronomicalCalculator<Instant> {
    override fun invoke(
        year: Int,
        month: Month,
        day: Int
    ): AstronomicalCalculator<Instant> {
        return object : AstronomicalCalculator<Instant> {
            override val sunTimes: RiseSetResult<Instant> = resultBuilder(day)
            override val moonTimes: RiseSetResult<Instant> = resultBuilder(day)
            override val moonPhase: MoonPhase? = null
        }
    }
}
