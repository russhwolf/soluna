package com.russhwolf.soluna.mobile.screen.settings

import app.cash.turbine.test
import com.russhwolf.soluna.mobile.screen.expectViewModelEvent
import com.russhwolf.soluna.mobile.screen.expectViewModelState
import com.russhwolf.soluna.mobile.screen.stateAndEvents
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsViewModelTest {
    private val viewModel by lazy {
        SettingsViewModel(Dispatchers.Unconfined)
            .also { it.activate() }
    }

    @Test
    fun navigate_locationList() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(SettingsViewModel.State, expectViewModelState())
            expectNoEvents()

            viewModel.performAction(SettingsViewModel.Action.Locations)
            assertEquals(SettingsViewModel.Event.Locations, expectViewModelEvent())
        }
    }

    @Test
    fun navigate_reminderList() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(SettingsViewModel.State, expectViewModelState())
            expectNoEvents()

            viewModel.performAction(SettingsViewModel.Action.Reminders)
            assertEquals(SettingsViewModel.Event.Reminders, expectViewModelEvent())
        }
    }
}
