package com.russhwolf.soluna.mobile.screen

import com.russhwolf.soluna.mobile.util.EventTrigger
import com.russhwolf.soluna.mobile.util.SupervisorScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

abstract class BaseViewModel<S>(initialState: S, dispatcher: CoroutineDispatcher) {
    val coroutineScope = SupervisorScope(dispatcher)

    private var viewStateListener: ViewStateListener<S>? = null
    private var loadingListener: LoadingListener? = null
    private var errorListener: ErrorListener? = null

    private var loadCount: Int by Delegates.observable(0) { _, _, newValue ->
        isLoading = newValue > 0
    }

    protected var state: S by Delegates.observable(initialState) { _, _, newValue ->
        viewStateListener?.invoke(newValue)
    }

    private var isLoading: Boolean by Delegates.observable(false) { _, _, newValue ->
        loadingListener?.invoke(newValue)
    }

    protected var error: EventTrigger<Throwable> by Delegates.observable(EventTrigger.empty()) { _, _, newValue ->
        newValue.consume { errorListener?.invoke(it) }
    }

    protected fun updateAsync(action: suspend () -> S): Job = doAsync { state = action() }

    protected fun doAsync(action: suspend () -> Unit): Job =
        coroutineScope.launch {
            loadCount++
            try {
                action()
            } catch (e: Throwable) {
                error = EventTrigger.create(e)
            }
            loadCount--
        }

    protected fun update(action: () -> S) =
        try {
            state = action()
        } catch (e: Throwable) {
            error = EventTrigger.create(e)
        }

    protected fun <T> Flow<T>.collectAndUpdate(action: (T) -> S) {
        coroutineScope.launch {
            collectLatest { update { action(it) } }
        }
    }

    fun setViewStateListener(listener: ViewStateListener<S>) {
        viewStateListener = listener
        listener.invoke(state)
    }

    fun setLoadingListener(listener: LoadingListener) {
        loadingListener = listener
        listener.invoke(isLoading)
    }

    fun setErrorListener(listener: ErrorListener) {
        errorListener = listener
        error.consume { listener(it) }
    }

    fun clearScope() {
        coroutineScope.clear()
    }
}

typealias ViewStateListener<S> = (S) -> Unit
typealias LoadingListener = (Boolean) -> Unit
typealias ErrorListener = (Throwable) -> Unit


