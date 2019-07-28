package com.russhwolf.soluna.mobile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

fun runBlockingTest(block: suspend CoroutineScope.() -> Unit): Unit = runBlocking(block = block)

/**
 * Create a near-instantaneous suspension point so other coroutines can update
 */
suspend fun pause() = delay(1)
