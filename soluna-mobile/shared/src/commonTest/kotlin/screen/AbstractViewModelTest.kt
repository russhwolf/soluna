package com.russhwolf.soluna.mobile.screen

import co.touchlab.stately.ensureNeverFrozen
import com.russhwolf.soluna.mobile.runBlocking
import kotlinx.coroutines.CompletableDeferred
import kotlin.properties.Delegates
import kotlin.test.assertEquals

abstract class AbstractViewModelTest<VM : BaseViewModel<T>, T : Any> {
    init {
        ensureNeverFrozen()
    }

    protected lateinit var state: T
    private var isLoadingSet = false
    protected var isLoading: Boolean by Delegates.notNull()
    protected var error: Throwable? = null

    private var loadingDeferred: CompletableDeferred<Unit>? = null

    protected val viewModel: VM by lazy {
        runBlocking {
            val viewModel = createViewModel()
            viewModel.setViewStateListener { state = it }
            viewModel.setLoadingListener {
                if (!isLoadingSet || isLoading != it) {
                    isLoadingSet = true
                    isLoading = it
                    updateLoadingContinuation(it)
                }
            }
            viewModel.setErrorListener { error = it }
            viewModel
        }
    }

    protected suspend fun awaitLoading() {
        initializeViewModel()
        loadingDeferred?.await()
    }

    abstract suspend fun createViewModel(): VM

    protected fun initializeViewModel() {
        viewModel // force lazy prop to load
    }

    private fun updateLoadingContinuation(isLoading: Boolean) {
        loadingDeferred = if (isLoading) {
            loadingDeferred?.cancel()
            CompletableDeferred()
        } else {
            loadingDeferred?.complete(Unit)
            null
        }
    }

    protected fun assertState(state: T, isLoading: Boolean = false, error: Throwable? = null) {
        // TODO messages might not match across platforms, eg NumberFormatException
//        assertEquals(error?.message, this.error?.message)
        assertEquals(error?.let { it::class }, this.error?.let { it::class })
        assertEquals(isLoading, this.isLoading)
        assertEquals(state, this.state)
    }
}

class TestError : RuntimeException("Test Error!")
