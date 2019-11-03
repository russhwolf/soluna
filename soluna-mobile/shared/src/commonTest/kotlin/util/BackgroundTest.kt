package com.russhwolf.soluna.mobile.util

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import com.russhwolf.soluna.mobile.AndroidJUnit4
import com.russhwolf.soluna.mobile.RunWith
import com.russhwolf.soluna.mobile.blockUntilIdle
import com.russhwolf.soluna.mobile.suspendTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BackgroundTest {
    @Test
    fun mainThread() {
        assertTrue(isMainThread)
    }

    @Test
    fun backgroundThread() = suspendTest {
        val isMain = runInBackground { isMainThread }
        assertFalse(isMain)
    }

    @Test
    fun mainToMain() {
        val isMain = AtomicReference<Boolean?>(null)
        runInMainThread({ isMain }) {
            it.value = isMainThread
        }
        blockUntilIdle()
        assertEquals(true, isMain.value)
    }

    @Test
    fun backgroundToMain() = suspendTest {
        val isMain = AtomicReference<Boolean?>(null)
        runInBackground {
            runInMainThread({ isMain }) {
                it.value = isMainThread
            }
        }
        blockUntilIdle()
        assertEquals(true, isMain.value)
    }

}
