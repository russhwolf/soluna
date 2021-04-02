package com.russhwolf.soluna.mobile

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

fun suspendTest(block: suspend CoroutineScope.() -> Unit): Unit =
    runBlocking(context = EmptyCoroutineContext, block = block)

