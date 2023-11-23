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

class FlowAdapter<T : Any>(
    private val scope: CoroutineScope,
    private val flow: Flow<T>
) {
    fun subscribe(
        onEvent: (T) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
    ): Job =
        flow
            .onEach { onEvent(it) }
            .catch { onError(it) }
            .onCompletion { onComplete() }
            .launchIn(scope)
}

class SuspendAdapter<T : Any>(
    private val scope: CoroutineScope,
    private val suspender: suspend () -> T
) {
    fun subscribe(
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ): Job =
        scope.launch {
            try {
                onSuccess(suspender())
            } catch (error: CancellationException) {
                // Don't call error block on cancellation
            } catch (error: Throwable) {
                onError(error)
            }
        }
}
