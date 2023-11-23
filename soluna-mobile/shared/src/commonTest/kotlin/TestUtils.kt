package com.russhwolf.soluna.mobile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds

fun suspendTest(block: suspend CoroutineScope.() -> Unit): Unit =
    runBlocking(context = EmptyCoroutineContext) {
        withTimeout(10.seconds) {
            block()
        }
    }

