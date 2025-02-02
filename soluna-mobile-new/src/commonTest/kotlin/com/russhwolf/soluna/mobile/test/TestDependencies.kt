package com.russhwolf.soluna.mobile.test

import app.cash.sqldelight.db.SqlDriver
import com.russhwolf.soluna.AstronomicalCalculator
import com.russhwolf.soluna.MoonPhase
import com.russhwolf.soluna.RiseSetResult
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone

expect fun createInMemorySqlDriver(): SqlDriver

class TestClock(ticks: Sequence<Instant>) : Clock {
    constructor(ticks: List<Instant>) : this(ticks.asSequence() + sequence { while (true) yield(ticks.last()) })

    constructor() : this(sequence {
        var i = 0L
        while (true) {
            yield(Instant.fromEpochMilliseconds(i++))
        }
    })

    private val iterator = ticks.iterator()
    private var tick = iterator.next()

    override fun now(): Instant {
        return tick
    }

    fun tick() {
        tick = iterator.next()
    }
}

class TestAstronomicalCalculatorFactory(private val resultBuilder: (Int) -> RiseSetResult<Instant>) :
        (TimeZone, Double, Double) -> (Int, Month, Int) -> AstronomicalCalculator<Instant> {
    override fun invoke(
        timeZone: TimeZone,
        latitude: Double,
        longitude: Double
    ): (Int, Month, Int) -> AstronomicalCalculator<Instant> {
        return { year, month, day ->
            object : AstronomicalCalculator<Instant> {
                override val sunTimes: RiseSetResult<Instant> = resultBuilder(day)
                override val moonTimes: RiseSetResult<Instant> = resultBuilder(day)
                override val moonPhase: MoonPhase? = null
            }
        }
    }
}
