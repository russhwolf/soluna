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
    fun loadingAction() {
        viewModel.awaitAsyncState()
        assertEquals("Initial", state)
        assertTrue(isLoading)
        assertNull(error)
    }

    @Test
    fun loadingUpdate() {
        viewModel.awaitAsyncState()
        assertEquals("Initial", state)
        assertTrue(isLoading)
        assertNull(error)
    }

    @Test
    fun doActionAsync() {
        viewModel.awaitAsyncAction()
        viewModel.resumeAsyncState { "Unused" }
        assertEquals("Initial", state)
        assertFalse(isLoading)
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

    @Test
    fun multipleLoads() {
        viewModel.awaitAsyncState(1)
        viewModel.awaitAsyncState(2)
        assertTrue(isLoading)
        viewModel.resumeAsyncState(1) { "Updating" }
        assertTrue(isLoading)
        viewModel.resumeAsyncState(2) { "Finished" }
        assertFalse(isLoading)
    }
}

class TestViewModel(state: String) : BaseViewModel<String>(state, Dispatchers.Unconfined) {
    private val continuations = mutableMapOf<Int, Continuation<String>>()

    fun updateState(updater: () -> String) = update { updater() }

    fun awaitAsyncAction(id: Int = 0) = doAsync {
        suspendCoroutine<String> {
            continuations[id] = it
        }
    }

    fun awaitAsyncState(id: Int = 0) = updateAsync {
        suspendCoroutine {
            continuations[id] = it
        }
    }

    fun resumeAsyncState(id: Int = 0, updater: () -> String) {
        val continuation = continuations[id]!!
        try {
            continuation.resume(updater())
        } catch (throwable: Throwable) {
            continuation.resumeWithException(throwable)
        }
    }
}
