package com.russhwolf.soluna.mobile.util

import kotlin.test.Test
import kotlin.test.assertEquals

class EventTriggerTest {
    @Test
    fun consumeWithLambda_populated() {
        val data = EventTrigger.create("foo")
        var consumedData: String? = null
        data.consume { consumedData = it }
        assertEquals("foo", consumedData)
    }

    @Test
    fun consumeWithLambda_empty() {
        val data = EventTrigger.empty<String>()
        var consumedData: String? = null
        data.consume { consumedData = it }
        assertEquals(null, consumedData)
    }
}
