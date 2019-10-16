package com.russhwolf.soluna.mobile.screen

import com.russhwolf.soluna.mobile.util.EventTrigger
import com.russhwolf.soluna.mobile.util.SupervisorScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

abstract class BaseViewModel<T>(initialState: T, dispatcher: CoroutineDispatcher) {
    protected val coroutineScope = SupervisorScope(dispatcher)

    private var viewStateListener: ViewStateListener<T>? = null
    private var loadingListener: LoadingListener? = null
    private var errorListener: ErrorListener? = null

    private var loadCount: Int = 0

    protected var state: T by Delegates.observable(initialState) { _, _, newValue ->
        viewStateListener?.invoke(newValue)
    }

    protected var isLoading: Boolean by Delegates.observable(false) { _, _, newValue ->
        loadingListener?.invoke(newValue)
    }

    protected var error: EventTrigger<Throwable> by Delegates.observable(EventTrigger.empty()) { _, _, newValue ->
        newValue.consume { errorListener?.invoke(it) }
    }

    protected fun updateAsync(action: suspend () -> T): Job =
        coroutineScope.launch {
            loadCount++
            isLoading = loadCount > 0
            try {
                state = action()
            } catch (e: Throwable) {
                error = EventTrigger.create(e)
            }
            loadCount--
            isLoading = loadCount > 0
        }

    protected fun update(action: () -> T) {
        try {
            state = action()
        } catch (e: Throwable) {
            error = EventTrigger.create(e)
        }
    }

    fun setViewStateListener(listener: ViewStateListener<T>) {
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

typealias ViewStateListener<T> = (T) -> Unit
typealias LoadingListener = (Boolean) -> Unit
typealias ErrorListener = (Throwable) -> Unit


