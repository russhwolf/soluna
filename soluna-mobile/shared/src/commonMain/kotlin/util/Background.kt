package com.russhwolf.soluna.mobile.util

import com.autodesk.coroutineworker.CoroutineWorker
import kotlinx.coroutines.Dispatchers

suspend fun <T> runInBackground(block: () -> T): T =
    CoroutineWorker.withContext(Dispatchers.Default) { block() }
