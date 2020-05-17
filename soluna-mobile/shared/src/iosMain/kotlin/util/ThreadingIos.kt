package com.russhwolf.soluna.mobile.util

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.staticCFunction
import platform.Foundation.NSThread
import platform.darwin.dispatch_async_f
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.DetachedObjectGraph
import kotlin.native.concurrent.attach
import kotlin.native.concurrent.freeze

actual val isMainThread: Boolean get() = NSThread.isMainThread

private class Task<T>(val input: T, val block: (T) -> Unit)

actual fun <T> runInMainThread(input: () -> T, block: (T) -> Unit) {
    dispatch_async_f(dispatch_get_main_queue(), DetachedObjectGraph {
        Task(input(), block).freeze()
    }.asCPointer(), staticCFunction { cPointer: COpaquePointer? ->
        val result = DetachedObjectGraph<Task<T>>(cPointer).attach()
        result.block(result.input)
    })
}
