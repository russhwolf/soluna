package com.russhwolf.soluna.mobile.screen.locationdetail

import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import com.russhwolf.soluna.mobile.util.mainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async

class LocationDetailViewModel(
    private val id: Long,
    private val repository: SolunaRepository,
    dispatcher: CoroutineDispatcher = mainDispatcher
) :
    BaseViewModel<LocationDetailViewState>(LocationDetailViewState(null), dispatcher) {

    fun refresh() = updateAsync {
        val locationAsync = coroutineScope.async { repository.getLocation(id) }
        val remindersAsync = coroutineScope.async { repository.getReminders(id) }
        it.copy(location = locationAsync.await(), reminders = remindersAsync.await())
    }

    fun delete() = updateAsync {
        repository.deleteLocation(id)
        it.copy(exitTrigger = EventTrigger.create())
    }
}

data class LocationDetailViewState(
    val location: Location?,
    val reminders: List<ReminderWithLocation> = emptyList(),
    val exitTrigger: EventTrigger<Unit> = EventTrigger.empty()
)
