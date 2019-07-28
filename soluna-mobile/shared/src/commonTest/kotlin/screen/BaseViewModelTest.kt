package com.russhwolf.soluna.mobile.screen

import com.russhwolf.soluna.mobile.runBlocking
import com.russhwolf.soluna.mobile.runBlockingTest
import kotlinx.coroutines.delay
import kotlin.coroutines.suspendCoroutine
import kotlin.properties.Delegates
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class BaseViewModelTest {

    private var state: String = "Initial"
    private var isLoading: Boolean by Delegates.notNull()
    private var error: Throwable? = null

    private var viewModel = TestViewModel(state)

    init {
        viewModel.setViewStateListener { state = it }
        viewModel.setLoadingListener { isLoading = it }
        viewModel.setErrorListener { error = it }
    }

    @Test
    fun initialState() {
        assertEquals("Initial", state)
        assertFalse(isLoading)
        assertNull(error)
    }

    @Test
    fun setState() {
        viewModel.setState("Updated")
        assertEquals("Updated", state)
        assertFalse(isLoading)
        assertNull(error)
    }

    @Test
    fun loadState() = runBlocking {
        viewModel.loadState("Updated")
        suspend()
        assertEquals("Updated", state)
        assertFalse(isLoading)
        assertNull(error)
    }

    @Test
    fun throwError() {
        viewModel.throwError()
        assertEquals("Initial", state)
        assertFalse(isLoading)
        assertNotNull(error)
    }

    @Test
    fun throwLoadError() = runBlockingTest {
        viewModel.throwLoadError()
        suspend()
        assertEquals("Initial", state)
        assertFalse(isLoading)
        assertNotNull(error)
    }

    @Suppress("DeferredResultUnused")
    @Test
    fun infiniteLoad() = runBlocking {
        viewModel.infiniteLoad()
        suspend()
        assertEquals("Initial", state)
        assertTrue(isLoading)
        assertNull(error)
    }
}

class TestViewModel(state: String) : BaseViewModel<String>(state) {
    fun setState(state: String) = update { state }

    fun loadState(state: String) = load { state }

    fun throwError() = update { throw TestError() }

    fun throwLoadError() = load { throw TestError() }

    fun infiniteLoad() {
        load {
            suspendCoroutine {
                // Continuation is never called
            }
        }
    }
}

private suspend fun suspend() = delay(1) // Create a near-instantaneous suspension point so listeners can update

class TestError : RuntimeException("Test Error!")
