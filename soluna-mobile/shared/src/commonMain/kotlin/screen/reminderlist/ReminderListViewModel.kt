package com.russhwolf.soluna.mobile.screen.reminderlist

import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.CoroutineDispatcher

class ReminderListViewModel(
    private val reminderRepository: ReminderRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<ReminderListViewState>(ReminderListViewState(), dispatcher) {
    init {
        reminderRepository
            .getRemindersFlow()
            .collectAndUpdate { state.copy(reminders = it) }

        updateAsync {
            val reminders = reminderRepository.getReminders()
            state.copy(reminders = reminders)
        }
    }

    fun addReminder(type: ReminderType, minutesBefore: Int) = doAsync {
        reminderRepository.addReminder(type, minutesBefore, enabled = true)
    }

    fun deleteReminder(reminderId: Long) = doAsync {
        reminderRepository.deleteReminder(reminderId)
    }

    fun setReminderEnabled(reminderId: Long, enabled: Boolean) = doAsync {
        reminderRepository.updateReminder(reminderId, enabled = enabled)
    }

    fun setReminderMinutesBefore(reminderId: Long, minutesBefore: Int) = doAsync {
        reminderRepository.updateReminder(reminderId, minutesBefore = minutesBefore)
    }
}

data class ReminderListViewState(
    val reminders: List<Reminder> = emptyList(),
    val addReminderTrigger: EventTrigger<Unit> = EventTrigger.empty(),
    val exitTrigger: EventTrigger<Unit> = EventTrigger.empty()
)
