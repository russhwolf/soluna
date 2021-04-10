package com.russhwolf.soluna.mobile.screen.locationdetail

import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.SelectableLocation
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationDetailViewModel(
    private val locationId: Long,
    private val locationRepository: LocationRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<LocationDetailViewModel.State, LocationDetailViewModel.Event, LocationDetailViewModel.Action>(
    State(null),
    dispatcher
) {
    init {
        locationRepository
            .getLocation(locationId)
            .onEach { emitState(State(it)) }
            .launchIn(coroutineScope)
    }

    override suspend fun performAction(action: Action) = when (action) {
        is Action.SetLabel -> setLabel(action.label)
        is Action.ToggleSelected -> toggleSelected()
        is Action.Delete -> delete()
    }

    private suspend fun setLabel(label: String) {
        locationRepository.updateLocationLabel(locationId, label)
    }

    private suspend fun toggleSelected() {
        locationRepository.toggleSelectedLocation(locationId)
    }

    private suspend fun delete() {
        locationRepository.deleteLocation(locationId)
        emitEvent(Event.Exit)
    }


    data class State(
        val location: SelectableLocation?
    )

    sealed class Event {
        object Exit : Event()
    }

    sealed class Action {
        data class SetLabel(val label: String) : Action()
        object ToggleSelected : Action()
        object Delete : Action()
    }

}
