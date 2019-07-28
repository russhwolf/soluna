package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel

class AddLocationViewModel(private val repository: SolunaRepository) : BaseViewModel<Unit>(Unit) {
    fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) {
        // TODO: using the parameter name "label" causes a compiler error in native on 1.3.4x,
        //  but appears fixed in 1.3.50 eap
        @Suppress("UnnecessaryVariable")
        val renamed = label
        load {
            repository.addLocation(renamed, latitude, longitude, timeZone)
        }
    }
}
