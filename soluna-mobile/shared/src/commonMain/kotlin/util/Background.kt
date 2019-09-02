package com.russhwolf.soluna.mobile.util

import com.autodesk.coroutineworker.CoroutineWorker

suspend fun <T> runInBackground(block: () -> T): T =
    CoroutineWorker.performAndWait { block() }
