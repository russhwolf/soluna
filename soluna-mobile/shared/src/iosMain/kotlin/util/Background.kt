package com.russhwolf.soluna.mobile.util

import platform.Foundation.NSThread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

private val backgroundWorker = Worker.start()

actual suspend fun <T> runInBackground(block: () -> T) = suspendCoroutine<T> { continuation ->
    val future = backgroundWorker.execute(TransferMode.SAFE, { block.freeze() }, { it() })
    try {
        future.consume { continuation.resume(it) }
    } catch (e: Throwable) {
        continuation.resumeWithException(e)
    }
}

actual val isMainThread: Boolean get() = NSThread.isMainThread
