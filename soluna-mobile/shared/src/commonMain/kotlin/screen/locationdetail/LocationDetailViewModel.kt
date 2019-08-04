package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.LocationDetail
import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel

class LocationDetailViewModel(id: Long, repository: SolunaRepository) :
    BaseViewModel<LocationDetailViewState>(LocationDetailViewState(null)) {

    init {
        updateAsync {
            val location = repository.getLocation(id)
            LocationDetailViewState(location)
        }
    }
}

data class LocationDetailViewState(val location: LocationDetail?)
