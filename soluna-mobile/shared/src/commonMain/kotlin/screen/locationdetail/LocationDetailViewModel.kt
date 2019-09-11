package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.EventTrigger
import com.russhwolf.soluna.mobile.util.mainDispatcher
import kotlinx.coroutines.CoroutineDispatcher

class LocationDetailViewModel(
    private val id: Long,
    private val repository: SolunaRepository,
    dispatcher: CoroutineDispatcher = mainDispatcher
) :
    BaseViewModel<LocationDetailViewState>(LocationDetailViewState(null), dispatcher) {

    val initialLoad = updateAsync {
        val location = repository.getLocation(id)
        LocationDetailViewState(location)
    }
}

data class LocationDetailViewState(
    val location: Location?,
    val exitTrigger: EventTrigger<Unit> = EventTrigger.empty()
)
