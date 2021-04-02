package com.russhwolf.soluna.mobile.screen.locationlist

import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.SelectableLocationSummary
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationListViewModel(
    private val locationRepository: LocationRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<LocationListViewModel.State, LocationListViewModel.Event, LocationListViewModel.Action>(
    State(emptyList()),
    dispatcher
) {

    init {
        locationRepository
            .getLocations()
            .onEach { emitState(State(it)) }
            .launchIn(coroutineScope)
    }

    override suspend fun performAction(action: Action) = when (action) {
        is Action.LocationDetails -> navigateToLocationDetails(action.locationId)
        is Action.RemoveLocation -> removeLocation(action.locationId)
        Action.AddLocation -> navigateToAddLocation()
    }

    private suspend fun navigateToLocationDetails(locationId: Long) = emitEvent(Event.LocationDetails(locationId))

    private suspend fun removeLocation(id: Long) = locationRepository.deleteLocation(id)

    private suspend fun navigateToAddLocation() = emitEvent(Event.AddLocation)

    data class State(
        val locations: List<SelectableLocationSummary>
    )

    sealed class Event {
        data class LocationDetails(val locationId: Long) : Event()
        object AddLocation : Event()
    }

    sealed class Action {
        data class LocationDetails(val locationId: Long) : Action()
        data class RemoveLocation(val locationId: Long) : Action()
        object AddLocation : Action()
    }
}

