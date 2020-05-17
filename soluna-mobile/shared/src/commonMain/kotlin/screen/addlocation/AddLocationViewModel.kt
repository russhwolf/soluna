package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.repository.GeocodeData
import com.russhwolf.soluna.mobile.repository.GeocodeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job

class AddLocationViewModel(
    private val locationRepository: LocationRepository,
    private val geocodeRepository: GeocodeRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<AddLocationViewState>(AddLocationViewState(), dispatcher) {

    fun addLocation(label: String, latitude: String, longitude: String, timeZone: String): Job = updateAsync {
        val latitudeDouble = latitude.toDouble()
        val longitudeDouble = longitude.toDouble()

        locationRepository.addLocation(label, latitudeDouble, longitudeDouble, timeZone)
        state.copy(exitTrigger = EventTrigger.create())
    }

    fun geocodeLocation(location: String) = updateAsync {
        val geocodeData = geocodeRepository.geocodeLocation(location)
        if (geocodeData != null) {
            state.copy(geocodeTrigger = EventTrigger.create(geocodeData))
        } else {
            state
        }
    }
}

data class AddLocationViewState(
    val geocodeTrigger: EventTrigger<GeocodeData> = EventTrigger.empty(),
    val exitTrigger: EventTrigger<Unit> = EventTrigger.empty()
)
