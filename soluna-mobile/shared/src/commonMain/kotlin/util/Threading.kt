package com.russhwolf.soluna.mobile.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

expect val isMainThread: Boolean

expect fun <T> runInMainThread(input: () -> T, block: (T) -> Unit)

fun runInMainThread(block: () -> Unit) = runInMainThread({ Unit }) { block() }

suspend fun <T> runInBackground(block: suspend () -> T): T = withContext(Dispatchers.Default) { block() }
