package com.russhwolf.soluna.mobile.screen.settings

import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher

class SettingsViewModel(
    dispatcher: CoroutineDispatcher
) : BaseViewModel<SettingsViewModel.State, SettingsViewModel.Event, SettingsViewModel.Action>(
    State,
    dispatcher
) {
    override fun activate() {
    }

    override suspend fun performAction(action: Action) = when (action) {
        Action.Locations -> navigateToLocationList()
        Action.Reminders -> navigateToReminderList()
    }

    private suspend fun navigateToLocationList() {
        emitEvent(Event.Locations)
    }

    private suspend fun navigateToReminderList() {
        emitEvent(Event.Reminders)
    }

    object State

    enum class Event {
        Locations,
        Reminders
    }

    enum class Action {
        Locations,
        Reminders
    }
}
