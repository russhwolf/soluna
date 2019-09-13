package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.GeocodeData
import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import com.russhwolf.soluna.mobile.util.mainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job

class AddLocationViewModel(private val repository: SolunaRepository, dispatcher: CoroutineDispatcher = mainDispatcher) :
    BaseViewModel<AddLocationViewState>(AddLocationViewState(), dispatcher) {
    fun addLocation(label: String, latitude: String, longitude: String, timeZone: String): Job = updateAsync {
        val latitudeDouble = latitude.toDouble()
        val longitudeDouble = longitude.toDouble()

        repository.addLocation(label, latitudeDouble, longitudeDouble, timeZone)
        state.copy(exitTrigger = EventTrigger.create())
    }

    fun geocodeLocation(location: String) = updateAsync {
        val geocodeData = repository.geocodeLocation(location)
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
