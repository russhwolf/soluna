package com.russhwolf.soluna.mobile.screen

import com.russhwolf.soluna.mobile.runBlocking
import kotlin.properties.Delegates

abstract class AbstractViewModelTest<VM : BaseViewModel<T>, T : Any> {
    protected lateinit var state: T
    protected var isLoading: Boolean by Delegates.notNull()
    protected var error: Throwable? = null

    protected val viewModel: VM by lazy {
        runBlocking {
            val viewModel = createViewModel()
            viewModel.setViewStateListener { state = it }
            viewModel.setLoadingListener { isLoading = it }
            viewModel.setErrorListener { error = it }
            viewModel
        }
    }

    abstract suspend fun createViewModel(): VM

    fun initializeViewModel() {
        viewModel // force lazy prop to load
    }
}

class TestError : RuntimeException("Test Error!")
