package com.russhwolf.soluna.mobile.screen.locationdetail

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import com.russhwolf.soluna.mobile.util.mainDispatcher
import kotlinx.coroutines.CoroutineDispatcher

// TODO this VM could use some debouncing
class LocationDetailViewModel(
    private val locationId: Long,
    private val locationRepository: LocationRepository,
    private val reminderRepository: ReminderRepository,
    dispatcher: CoroutineDispatcher = mainDispatcher
) :
    BaseViewModel<LocationDetailViewState>(LocationDetailViewState(null), dispatcher) {

    init {
        locationRepository
            .getLocationFlow(locationId)
            .collectAndUpdate { state.copy(location = it) }

        reminderRepository
            .getRemindersForLocationFlow(locationId)
            .collectAndUpdate { state.copy(reminders = it) }

        updateAsync {
            val location = locationRepository.getLocation(locationId)
            state.copy(location = location)
        }

        updateAsync {
            val reminders = reminderRepository.getRemindersForLocation(locationId)
            state.copy(reminders = reminders)
        }
    }

    fun setLabel(label: String) = doAsync {
        locationRepository.updateLocationLabel(locationId, label)
    }

    fun delete() = updateAsync {
        locationRepository.deleteLocation(locationId)
        state.copy(exitTrigger = EventTrigger.create())
    }

    fun addReminder(type: ReminderType, minutesBefore: Int) = doAsync {
        reminderRepository.addReminder(locationId, type, minutesBefore, enabled = true)
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

data class LocationDetailViewState(
    val location: Location?,
    val reminders: List<ReminderWithLocation> = emptyList(),
    val addReminderTrigger: EventTrigger<Unit> = EventTrigger.empty(),
    val exitTrigger: EventTrigger<Unit> = EventTrigger.empty()
)
