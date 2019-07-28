package com.russhwolf.soluna.mobile

import kotlinx.coroutines.CoroutineScope

fun runBlockingTest(block: suspend CoroutineScope.() -> Unit): Unit = runBlocking(block = block)
