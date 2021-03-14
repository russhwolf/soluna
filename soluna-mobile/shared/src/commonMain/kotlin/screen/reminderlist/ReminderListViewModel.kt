package com.russhwolf.soluna.mobile.screen.reminderlist

import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ReminderListViewModel(
    private val reminderRepository: ReminderRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<ReminderListViewModel.State, ReminderListViewModel.Event, ReminderListViewModel.Action>(
    State(emptyList()),
    dispatcher
) {
    init {
        reminderRepository
            .getRemindersFlow()
            .onEach { emitState(State(it)) }
            .launchIn(coroutineScope)
    }

    override suspend fun performAction(action: Action) = when (action) {
        is Action.RemoveReminder -> deleteReminder(action.id)
        is Action.AddReminder -> addReminder(action.type, action.minutesBefore)
        is Action.SetReminderEnabled -> setReminderEnabled(action.id, action.enabled)
        is Action.SetReminderMinutesBefore -> setReminderMinutesBefore(action.id, action.minutesBefore)
        Action.Exit -> emitEvent(Event.Exit)
    }

    private suspend fun addReminder(type: ReminderType, minutesBefore: Int) {
        reminderRepository.addReminder(type, minutesBefore, enabled = true)
    }

    private suspend fun deleteReminder(reminderId: Long) {
        reminderRepository.deleteReminder(reminderId)
    }

    private suspend fun setReminderEnabled(reminderId: Long, enabled: Boolean) {
        reminderRepository.updateReminder(reminderId, enabled = enabled)
    }

    private suspend fun setReminderMinutesBefore(reminderId: Long, minutesBefore: Int) {
        reminderRepository.updateReminder(reminderId, minutesBefore = minutesBefore)
    }

    data class State(
        val locations: List<Reminder>
    )

    sealed class Event {
        object Exit : Event()
    }

    sealed class Action {
        data class RemoveReminder(val id: Long) : Action()
        data class AddReminder(val type: ReminderType, val minutesBefore: Int) : Action()
        data class SetReminderEnabled(val id: Long, val enabled: Boolean) : Action()
        data class SetReminderMinutesBefore(val id: Long, val minutesBefore: Int) : Action()
        object Exit : Action()
    }
}
