package com.russhwolf.soluna.mobile.util

import kotlinx.coroutines.CoroutineDispatcher

expect val isMainThread: Boolean

expect val mainDispatcher: CoroutineDispatcher

expect fun <T> runInMainThread(input: () -> T, block: (T) -> Unit)
