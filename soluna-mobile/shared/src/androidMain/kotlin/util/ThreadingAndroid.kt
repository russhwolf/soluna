package com.russhwolf.soluna.mobile.util

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val isMainThread: Boolean get() = Looper.myLooper() === Looper.getMainLooper()

actual val mainDispatcher: CoroutineDispatcher = Dispatchers.Main

actual fun <T> runInMainThread(input: () -> T, block: (T) -> Unit) {
    mainHandler.post {
        block(input())
    }
}

private val mainHandler = Handler(Looper.getMainLooper())
