package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.soluna.mobile.test.TestClock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class CurrentTimeRepositoryTest {
    @Test
    fun getCurrentTime() {
        val clock = TestClock()
        val repository = CurrentTimeRepository(clock)

        assertEquals(0L, repository.getCurrentTime().toEpochMilliseconds())
        assertEquals(0L, repository.getCurrentTime().toEpochMilliseconds())

        clock.tick()
        assertEquals(1L, repository.getCurrentTime().toEpochMilliseconds())
        assertEquals(1L, repository.getCurrentTime().toEpochMilliseconds())
    }

    @Test
    fun getCurrentTimeFlow() = runTest {
        val clock = TestClock()
        val repository = CurrentTimeRepository(clock)

        repository.getCurrentTimeFlow(1.seconds).test {
            assertEquals(0L, awaitItem().toEpochMilliseconds())
            assertEquals(0L, awaitItem().toEpochMilliseconds())

            clock.tick()
            assertEquals(1L, awaitItem().toEpochMilliseconds())
            assertEquals(1L, awaitItem().toEpochMilliseconds())
        }
    }
}

