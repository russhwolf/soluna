package com.russhwolf.soluna.mobile.screen.locationdetail

import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import com.russhwolf.soluna.mobile.util.mainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationDetailViewModel(
    private val locationId: Long,
    private val repository: SolunaRepository,
    dispatcher: CoroutineDispatcher = mainDispatcher
) :
    BaseViewModel<LocationDetailViewState>(LocationDetailViewState(null), dispatcher) {

    init {
        repository
            .getLocationFlow(locationId)
            .collectAndUpdate { state.copy(location = it) }

        repository
            .getRemindersForLocationFlow(locationId)
            .collectAndUpdate { state.copy(reminders = it) }

        updateAsync {
            val location = repository.getLocation(locationId)
            state.copy(location = location)
        }

        updateAsync {
            val reminders = repository.getRemindersForLocation(locationId)
            state.copy(reminders = reminders)
        }
    }

    fun delete() = updateAsync {
        repository.deleteLocation(locationId)
        state.copy(exitTrigger = EventTrigger.create())
    }
}

data class LocationDetailViewState(
    val location: Location?,
    val reminders: List<ReminderWithLocation> = emptyList(),
    val exitTrigger: EventTrigger<Unit> = EventTrigger.empty()
)
