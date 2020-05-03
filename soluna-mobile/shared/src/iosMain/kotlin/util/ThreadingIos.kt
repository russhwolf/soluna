package com.russhwolf.soluna.mobile.util

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.staticCFunction
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import platform.Foundation.NSThread
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_async
import platform.darwin.dispatch_async_f
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_t
import platform.darwin.dispatch_time
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.DetachedObjectGraph
import kotlin.native.concurrent.attach
import kotlin.native.concurrent.freeze

actual val isMainThread: Boolean get() = NSThread.isMainThread

actual val mainDispatcher: CoroutineDispatcher = NsQueueDispatcher(dispatch_get_main_queue())

// Adapted from https://github.com/Kotlin/kotlinx.coroutines/issues/470#issuecomment-440080970
@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
private class NsQueueDispatcher(
    private val dispatchQueue: dispatch_queue_t
) : CoroutineDispatcher(), Delay {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }

    @InternalCoroutinesApi
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000_000), dispatchQueue) {
            with(continuation) {
                resumeUndispatched(Unit)
            }
        }
    }

    @InternalCoroutinesApi
    override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
        val handle = object : DisposableHandle {
            var disposed = false
                private set

            override fun dispose() {
                disposed = true
            }
        }
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000_000), dispatchQueue) {
            if (!handle.disposed) {
                block.run()
            }
        }

        return handle
    }

}

private class Task<T>(val input: T, val block: (T) -> Unit)

actual fun <T> runInMainThread(input: () -> T, block: (T) -> Unit) {
    dispatch_async_f(dispatch_get_main_queue(), DetachedObjectGraph {
        Task(input(), block).freeze()
    }.asCPointer(), staticCFunction { cPointer: COpaquePointer? ->
        val result = DetachedObjectGraph<Task<T>>(cPointer).attach()
        result.block(result.input)
    })
}
