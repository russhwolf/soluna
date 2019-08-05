package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.LocationDetail
import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger

class LocationDetailViewModel(private val id: Long, private val repository: SolunaRepository) :
    BaseViewModel<LocationDetailViewState>(LocationDetailViewState(null)) {

    val initialLoad = updateAsync {
        val location = repository.getLocation(id)
        LocationDetailViewState(location)
    }
}

data class LocationDetailViewState(
    val location: LocationDetail?,
    val exitTrigger: EventTrigger<Unit> = EventTrigger.empty()
)
