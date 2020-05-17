package com.russhwolf.soluna.mobile.util

import android.os.Handler
import android.os.Looper

actual val isMainThread: Boolean get() = Looper.myLooper() === Looper.getMainLooper()

actual fun <T> runInMainThread(input: () -> T, block: (T) -> Unit) {
    mainHandler.post {
        block(input())
    }
}

private val mainHandler = Handler(Looper.getMainLooper())
