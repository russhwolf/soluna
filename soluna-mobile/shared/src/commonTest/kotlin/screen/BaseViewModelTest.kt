package com.russhwolf.soluna.mobile.screen

import com.russhwolf.soluna.mobile.pause
import com.russhwolf.soluna.mobile.runBlocking
import com.russhwolf.soluna.mobile.runBlockingTest
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
    fun setState() {
        viewModel.setState("Updated")
        assertEquals("Updated", state)
        assertFalse(isLoading)
        assertNull(error)
    }

    @Test
    fun loadState() = runBlocking {
        viewModel.loadState("Updated")
        pause()
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
        pause()
        assertEquals("Initial", state)
        assertFalse(isLoading)
        assertNotNull(error)
    }

    @Test
    fun infiniteLoad() = runBlocking {
        viewModel.infiniteLoad()
        pause()
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
