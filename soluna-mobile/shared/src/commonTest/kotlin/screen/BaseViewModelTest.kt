package com.russhwolf.soluna.mobile.screen

import app.cash.turbine.test
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class BaseViewModelTest {
    private val viewModel: TestViewModel = TestViewModel("initial")

    @Test
    fun initialState() {
        assertEquals("initial", viewModel.state.value)
    }

    @Test
    fun updateState() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals("initial", expectViewModelState())
            expectNoEvents()

            viewModel.performAction("state-update")
            assertEquals("update", expectViewModelState())
        }
    }

    @Test
    fun updateEvent() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals("initial", expectViewModelState())
            expectNoEvents()

            viewModel.performAction("event-trigger")
            assertEquals("trigger", expectViewModelEvent())
        }
    }

    @Test
    fun updateError() = suspendTest {
        val error = assertFails { viewModel.performAction("error") }
        assertEquals("invalid action error", error.message)
    }
}

class TestViewModel(state: String) : BaseViewModel<String, String, String>(state, Dispatchers.Unconfined) {
    override fun activate() {}

    override suspend fun performAction(action: String) {
        delay(10)
        when {
            action.startsWith("state-") -> emitState(action.removePrefix("state-"))
            action.startsWith("event-") -> emitEvent(action.removePrefix("event-"))
            else -> error("invalid action $action")
        }
    }
}
