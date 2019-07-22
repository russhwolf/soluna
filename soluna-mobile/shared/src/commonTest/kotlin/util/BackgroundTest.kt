package com.russhwolf.soluna.mobile.util

import com.russhwolf.soluna.mobile.AndroidJUnit4
import com.russhwolf.soluna.mobile.RunWith
import com.russhwolf.soluna.mobile.runBlocking
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BackgroundTest {
    @Test
    fun runInBackground() = runBlocking {
        assertTrue(isMainThread)
        val output = runInBackground {
            isMainThread
        }
        assertFalse(output)
    }
}
