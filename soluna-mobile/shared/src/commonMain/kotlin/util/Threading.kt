package com.russhwolf.soluna.mobile.util

import com.autodesk.coroutineworker.CoroutineWorker
import kotlinx.coroutines.Dispatchers

expect val isMainThread: Boolean

expect fun <T> runInMainThread(input: () -> T, block: (T) -> Unit)

fun runInMainThread(block: () -> Unit) = runInMainThread({ Unit }) { block() }

suspend fun <T> runInBackground(block: suspend () -> T): T =
    CoroutineWorker.withContext(Dispatchers.Default) { block() }
