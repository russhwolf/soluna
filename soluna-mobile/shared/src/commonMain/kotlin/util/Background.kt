package com.russhwolf.soluna.mobile.util

import kotlinx.coroutines.CoroutineDispatcher

expect suspend fun <T> runInBackground(block: () -> T): T

expect val isMainThread: Boolean

expect val mainDispatcher: CoroutineDispatcher
