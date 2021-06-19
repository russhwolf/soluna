package com.russhwolf.soluna.mobile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

fun suspendTest(block: suspend CoroutineScope.() -> Unit): Unit =
    runBlocking(context = EmptyCoroutineContext) {
        withTimeout(Duration.seconds(10)) {
            block()
        }
    }

