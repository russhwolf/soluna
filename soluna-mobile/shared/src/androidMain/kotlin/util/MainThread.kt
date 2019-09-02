package com.russhwolf.soluna.mobile.util

import android.os.Looper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val isMainThread: Boolean get() = Looper.myLooper() === Looper.getMainLooper()

actual val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
