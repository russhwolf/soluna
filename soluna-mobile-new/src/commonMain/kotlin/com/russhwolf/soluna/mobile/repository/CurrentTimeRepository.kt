package com.russhwolf.soluna.mobile.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class CurrentTimeRepository(private val clock: Clock) {
    fun getCurrentTime(): Instant = clock.now()

    fun getCurrentTimeFlow(coroutineScope: CoroutineScope, period: Duration): StateFlow<Instant> = flow {
        while (currentCoroutineContext().isActive) {
            delay(period)
            emit(clock.now())
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), clock.now())
}
