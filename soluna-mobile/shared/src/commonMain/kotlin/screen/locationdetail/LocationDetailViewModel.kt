package com.russhwolf.soluna.mobile.screen.locationdetail

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.CoroutineDispatcher

// TODO this VM could use some debouncing
class LocationDetailViewModel(
    private val locationId: Long,
    private val locationRepository: LocationRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<LocationDetailViewState>(LocationDetailViewState(null), dispatcher) {

    init {
        locationRepository
            .getLocationFlow(locationId)
            .collectAndUpdate { state.copy(location = it) }

        updateAsync {
            val location = locationRepository.getLocation(locationId)
            state.copy(location = location)
        }
    }

    fun setLabel(label: String) = doAsync {
        locationRepository.updateLocationLabel(locationId, label)
    }

    fun delete() = updateAsync {
        locationRepository.deleteLocation(locationId)
        state.copy(exitTrigger = EventTrigger.create())
    }
}

data class LocationDetailViewState(
    val location: Location?,
    val exitTrigger: EventTrigger<Unit> = EventTrigger.empty()
)
