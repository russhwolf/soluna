package com.russhwolf.soluna.mobile.util

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_async
import platform.darwin.dispatch_queue_t
import platform.darwin.dispatch_time
import kotlin.coroutines.CoroutineContext

// Adapted from https://github.com/Kotlin/kotlinx.coroutines/issues/470#issuecomment-440080970
@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class NsQueueDispatcher(
    private val dispatchQueue: dispatch_queue_t
) : CoroutineDispatcher(), Delay {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }

    @InternalCoroutinesApi
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        dispatch_after(
            dispatch_time(
                DISPATCH_TIME_NOW,
                timeMillis * 1_000_000
            ), dispatchQueue
        ) {
            with(continuation) {
                resumeUndispatched(Unit)
            }
        }
    }

    @InternalCoroutinesApi
    override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
        val handle = object : DisposableHandle {
            var disposed = false
                private set

            override fun dispose() {
                disposed = true
            }
        }
        dispatch_after(
            dispatch_time(
                DISPATCH_TIME_NOW,
                timeMillis * 1_000_000
            ), dispatchQueue
        ) {
            if (!handle.disposed) {
                block.run()
            }
        }

        return handle
    }

}
