package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class AddLocationViewModel(private val repository: SolunaRepository) :
    BaseViewModel<AddLocationViewState>(AddLocationViewState()) {
    fun addLocation(label: String, latitude: String, longitude: String, timeZone: String): Deferred<Unit> {
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
}

data class AddLocationViewState(
    val exitTrigger: EventTrigger<Unit> = EventTrigger.empty()
)
