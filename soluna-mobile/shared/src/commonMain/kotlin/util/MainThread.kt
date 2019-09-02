package com.russhwolf.soluna.mobile.util

import kotlinx.coroutines.CoroutineDispatcher

expect val isMainThread: Boolean

expect val mainDispatcher: CoroutineDispatcher
