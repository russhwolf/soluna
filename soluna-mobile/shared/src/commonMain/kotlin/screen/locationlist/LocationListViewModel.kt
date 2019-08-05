package com.russhwolf.soluna.mobile.screen.locationlist

import com.russhwolf.soluna.mobile.LocationSummary
import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger

class LocationListViewModel(private val repository: SolunaRepository) :
    BaseViewModel<LocationListViewState>(LocationListViewState(emptyList())) {

    val initialLoad = updateAsync {
        val locations = repository.getLocations()
        LocationListViewState(locations)
    }

    fun navigateToLocationDetails(locationSummary: LocationSummary) = update {
        it.copy(locationDetailsTrigger = EventTrigger.create(locationSummary.id))
    }


    fun navigateToAddLocation() = update {
        it.copy(addLocationTrigger = EventTrigger.create())
    }

    fun removeLocation(id: Long) = updateAsync {
        repository.deleteLocation(id)
        val locations = repository.getLocations()
        LocationListViewState(locations)
    }
}

data class LocationListViewState(
    val locations: List<LocationSummary>,
    val locationDetailsTrigger: EventTrigger<Long> = EventTrigger.empty(),
    val addLocationTrigger: EventTrigger<Unit> = EventTrigger.empty()
)

