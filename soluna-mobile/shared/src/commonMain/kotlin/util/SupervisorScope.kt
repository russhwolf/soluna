package com.russhwolf.soluna.mobile.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext

class SupervisorScope(dispatcher: CoroutineDispatcher) : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = job + dispatcher

    fun clear() {
        job.cancelChildren()
    }
}
