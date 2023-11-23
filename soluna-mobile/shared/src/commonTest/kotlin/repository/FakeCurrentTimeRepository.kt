package com.russhwolf.soluna.mobile.repository

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
class FakeCurrentTimeRepository(
    initialTime: Instant = LocalDateTime(2021, 1, 1, 0, 0).toInstant(TimeZone.UTC),
    emitImmediately: Boolean = true
) : CurrentTimeRepository {

    private val ticker = MutableSharedFlow<Duration>()
    private val currentInstant: SharedFlow<Instant> =
        ticker
            .scan(initialTime) { time, tick -> time + tick }
            .run {
                if (emitImmediately) {
                    stateIn(GlobalScope, SharingStarted.Eagerly, initialTime)
                } else {
                    shareIn(GlobalScope, SharingStarted.Eagerly)
                }
            }

    override fun getCurrentTime(): Instant = currentInstant.replayCache.last()

    override fun getCurrentTimeFlow(period: Duration): Flow<Instant> = currentInstant

    suspend fun tick(duration: Duration = 0.seconds) {
        ticker.emit(duration)
    }
}
