package com.russhwolf.soluna.mobile.test

import app.cash.sqldelight.db.SqlDriver
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

expect fun createInMemorySqlDriver(): SqlDriver

class TestClock(ticks: Sequence<Instant>) : Clock {
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
