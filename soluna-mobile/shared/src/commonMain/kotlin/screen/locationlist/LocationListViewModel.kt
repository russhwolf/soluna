package com.russhwolf.soluna.mobile.screen.locationlist

import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.CoroutineDispatcher

class LocationListViewModel(
    private val locationRepository: LocationRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<LocationListViewState>(LocationListViewState(emptyList()), dispatcher) {

    init {
        locationRepository
            .getLocationsFlow()
            .collectAndUpdate { state.copy(locations = it) }

        updateAsync {
            val locations = locationRepository.getLocations()
            state.copy(locations = locations)
        }
    }

    fun navigateToLocationDetails(locationSummary: LocationSummary) = update {
        state.copy(locationDetailsTrigger = EventTrigger.create(locationSummary.id))
    }


    fun navigateToAddLocation() = update {
        state.copy(addLocationTrigger = EventTrigger.create())
    }

    fun removeLocation(id: Long) = updateAsync {
        locationRepository.deleteLocation(id)
        val locations = locationRepository.getLocations()
        state.copy(locations = locations)
    }
}

data class LocationListViewState(
    val locations: List<LocationSummary>,
    val locationDetailsTrigger: EventTrigger<Long> = EventTrigger.empty(),
    val addLocationTrigger: EventTrigger<Unit> = EventTrigger.empty()
)

