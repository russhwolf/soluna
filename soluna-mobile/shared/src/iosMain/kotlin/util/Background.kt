package com.russhwolf.soluna.mobile.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import platform.Foundation.NSThread
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_t
import kotlin.coroutines.CoroutineContext
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

actual val mainDispatcher: CoroutineDispatcher = NsQueueDispatcher(dispatch_get_main_queue())

private class NsQueueDispatcher(
    private val dispatchQueue: dispatch_queue_t
) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }
}
