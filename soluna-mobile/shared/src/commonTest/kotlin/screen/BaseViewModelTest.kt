package com.russhwolf.soluna.mobile.screen

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class BaseViewModelTest : AbstractViewModelTest<TestViewModel, String>() {

    override suspend fun createViewModel(): TestViewModel {
        state = "Initial"
        return TestViewModel(state)
    }

    @Test
    fun initialState() {
        initializeViewModel()
        assertEquals("Initial", state)
        assertFalse(isLoading)
        assertNull(error)
    }

    @Test
    fun updateState() {
        viewModel.updateState { "Updated" }
        assertEquals("Updated", state)
        assertFalse(isLoading)
        assertNull(error)
    }

    @Test
    fun loading() {
        viewModel.awaitAsyncState()
        assertEquals("Initial", state)
        assertTrue(isLoading)
        assertNull(error)
    }

    @Test
    fun updateStateAsync() {
        viewModel.awaitAsyncState()
        viewModel.resumeAsyncState { "Updated" }
        assertEquals("Updated", state)
        assertFalse(isLoading)
        assertNull(error)
    }

    @Test
    fun throwError() {
        viewModel.updateState { throw TestError() }
        assertEquals("Initial", state)
        assertFalse(isLoading)
        assertNotNull(error)
    }

    @Test
    fun throwErrorAsync() {
        viewModel.awaitAsyncState()
        viewModel.resumeAsyncState { throw TestError() }
        assertEquals("Initial", state)
        assertFalse(isLoading)
        assertNotNull(error)
    }
}

class TestViewModel(state: String) : BaseViewModel<String>(state, Dispatchers.Unconfined) {
    private var continuation: Continuation<String>? = null

    fun updateState(updater: () -> String) = update { updater() }

    fun awaitAsyncState() = updateAsync {
        suspendCoroutine {
            continuation = it
        }
    }

    fun resumeAsyncState(updater: () -> String) {
        try {
            continuation!!.resume(updater())
        } catch (throwable: Throwable) {
            continuation!!.resumeWithException(throwable)
        }
    }
}
