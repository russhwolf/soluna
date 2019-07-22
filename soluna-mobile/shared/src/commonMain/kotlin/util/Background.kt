package com.russhwolf.soluna.mobile.util

expect suspend fun <T> runInBackground(block: () -> T): T

expect val isMainThread: Boolean
