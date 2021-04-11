package com.russhwolf.soluna.mobile.repository

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

interface CurrentTimeRepository {
    fun getCurrentTime(): Instant

    fun getCurrentTimeFlow(period: Duration): Flow<Instant>

    class Impl(private val clock: Clock) : CurrentTimeRepository {
        override fun getCurrentTime(): Instant = clock.now()

        override fun getCurrentTimeFlow(period: Duration): Flow<Instant> = flow {
            while (currentCoroutineContext().isActive) {
                emit(clock.now())
                delay(period)
            }
        }
    }
}
