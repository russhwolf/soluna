package com.russhwolf.soluna.mobile

import kotlinx.coroutines.CoroutineScope

fun suspendTest(block: suspend CoroutineScope.() -> Unit): Unit = runBlocking(block = block)

