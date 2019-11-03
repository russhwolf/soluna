package com.russhwolf.soluna.mobile.screen

import com.russhwolf.soluna.mobile.runBlocking
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.properties.Delegates

abstract class AbstractViewModelTest<VM : BaseViewModel<T>, T : Any> {
    protected lateinit var state: T
    protected var isLoading: Boolean by Delegates.notNull()
    protected var error: Throwable? = null

    private var loadingJob: Job? = null
    private var loadingContinuation: CancellableContinuation<Unit>? = null

    protected val viewModel: VM by lazy {
        runBlocking {
            val viewModel = createViewModel()
            viewModel.setViewStateListener { state = it }
            viewModel.setLoadingListener {
                isLoading = it
                updateLoadingContinuation(it)
            }
            viewModel.setErrorListener { error = it }
            viewModel
        }
    }

    protected suspend fun awaitLoading() {
        initializeViewModel()
        loadingJob?.join()
    }

    abstract suspend fun createViewModel(): VM

    protected fun initializeViewModel() {
        viewModel // force lazy prop to load
    }

    private fun updateLoadingContinuation(isLoading: Boolean) {
        if (isLoading) {
            loadingJob?.cancel()
            loadingJob = GlobalScope.launch {
                suspendCancellableCoroutine {
                    loadingContinuation?.cancel()
                    loadingContinuation = it
                    it.invokeOnCancellation {
                        loadingContinuation = null
                    }
                }
            }
        } else {
            loadingContinuation?.resume(Unit)
            loadingContinuation = null
        }
    }
}

class TestError : RuntimeException("Test Error!")
