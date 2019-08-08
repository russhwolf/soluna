package com.russhwolf.soluna.mobile.screen

import com.russhwolf.soluna.mobile.runBlocking
import com.russhwolf.soluna.mobile.runBlockingTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        viewModel.updateState("Updated")
        assertEquals("Updated", state)
        assertFalse(isLoading)
        assertNull(error)
    }

    @Test
    fun updateStateAsync() = runBlocking {
        viewModel.updateStateAsync("Updated").join()
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
    fun throwErrorAsync() = runBlockingTest {
        viewModel.throwErrorAsync().join()
        assertEquals("Initial", state)
        assertFalse(isLoading)
        assertNotNull(error)
    }

    @Test
    fun infiniteDelayAsync() = runBlocking {
        @Suppress("DeferredResultUnused")
        viewModel.infiniteDelayAsync()
        delay(5)
        assertEquals("Initial", state)
        assertTrue(isLoading)
        assertNull(error)
    }
}

class TestViewModel(state: String) : BaseViewModel<String>(state, Dispatchers.Unconfined) {
    fun updateState(state: String) = update { state }

    fun updateStateAsync(state: String) = updateAsync { state }

    fun throwError() = update { throw TestError() }

    fun throwErrorAsync() = updateAsync { throw TestError() }

    fun infiniteDelayAsync() = updateAsync { state ->
        delay(Long.MAX_VALUE)
        state
    }

}
