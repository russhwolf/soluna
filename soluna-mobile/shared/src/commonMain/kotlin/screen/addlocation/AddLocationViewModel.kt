package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.GeocodeData
import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import com.russhwolf.soluna.mobile.util.mainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

class AddLocationViewModel(private val repository: SolunaRepository, dispatcher: CoroutineDispatcher = mainDispatcher) :
    BaseViewModel<AddLocationViewState>(AddLocationViewState(), dispatcher) {
    fun addLocation(label: String, latitude: String, longitude: String, timeZone: String): Job {
        val latitudeDouble = latitude.toDoubleOrNull()
        val longitudeDouble = longitude.toDoubleOrNull()

        if (latitudeDouble == null || longitudeDouble == null) {
            // TODO error UI
            // (just no-op for now)
            return coroutineScope.async { Unit }
        }

        // TODO: passing a value with the name "label" causes a compiler error in native on 1.3.4x,
        //  but appears fixed in 1.3.50 eap
        @Suppress("UnnecessaryVariable")
        val renamed = label
        return updateAsync {
            repository.addLocation(renamed, latitudeDouble, longitudeDouble, timeZone)
            state.copy(exitTrigger = EventTrigger.create())
        }
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
