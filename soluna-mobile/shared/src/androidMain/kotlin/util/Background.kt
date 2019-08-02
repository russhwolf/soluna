package com.russhwolf.soluna.mobile.util

import android.os.Looper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun <T> runInBackground(block: () -> T): T = withContext(Dispatchers.IO) { block() }

actual val isMainThread: Boolean get() = Looper.myLooper() === Looper.getMainLooper()

actual val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
