package com.russhwolf.soluna.mobile.repository

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

interface ClockRepository {
    fun getCurrentTimeFlow(period: Duration): Flow<Instant>

    class Impl(val clock: Clock) : ClockRepository {
        override fun getCurrentTimeFlow(period: Duration): Flow<Instant> = flow {
            while (currentCoroutineContext().isActive) {
                emit(clock.now())
                delay(period)
            }
        }
    }
}
