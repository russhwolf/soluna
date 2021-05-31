package com.russhwolf.soluna.mobile

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.native.concurrent.freeze

class FlowAdapter<T : Any>(
    private val scope: CoroutineScope,
    private val flow: Flow<T>
) {
    init {
        freeze()
    }

    fun subscribe(
        onEvent: (T) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
    ): Job =
        flow
            .onEach { onEvent(it.freeze()) }
            .catch { onError(it.freeze()) }
            .onCompletion { onComplete() }
            .launchIn(scope)
}

class NullableFlowAdapter<T>(
    private val scope: CoroutineScope,
    private val flow: Flow<T>
) {
    init {
        freeze()
    }

    fun subscribe(
        onEvent: (T) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
    ): Job =
        flow
            .onEach { onEvent(it.freeze()) }
            .catch { onError(it.freeze()) }
            .onCompletion { onComplete() }
            .launchIn(scope)
}

class SuspendAdapter<T : Any>(
    private val scope: CoroutineScope,
    private val suspender: suspend () -> T
) {
    init {
        freeze()
    }

    fun subscribe(
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ): Job =
        scope.launch {
            try {
                onSuccess(suspender().freeze())
            } catch (error: CancellationException) {
                // Don't call error block on cancellation
            } catch (error: Throwable) {
                onError(error.freeze())
            }
        }.freeze()
}
