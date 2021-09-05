package com.russhwolf.soluna.mobile.screen.locationdetail

import com.russhwolf.soluna.mobile.repository.CurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.SelectableLocation
import com.russhwolf.soluna.mobile.repository.UpcomingTimesRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.time.Duration

class LocationDetailViewModel(
    private val locationId: Long,
    private val locationRepository: LocationRepository,
    private val upcomingTimesRepository: UpcomingTimesRepository,
    private val currentTimeRepository: CurrentTimeRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<LocationDetailViewModel.State, LocationDetailViewModel.Event, LocationDetailViewModel.Action>(
    State.Loading,
    dispatcher
) {
    override fun activate() {
        locationRepository.getLocation(locationId).flatMapLatest { selectedLocation ->
            if (selectedLocation != null) {
                combine(
                    flowOf(selectedLocation),
                    upcomingTimesRepository.getUpcomingTimes(selectedLocation),
                    currentTimeRepository.getCurrentTimeFlow(Duration.seconds(1))
                ) { location, upcomingTimes, currentTime ->
                    val timeZone = if (location.timeZone in TimeZone.availableZoneIds) {
                        TimeZone.of(location.timeZone)
                    } else {
                        // TODO better error handling
                        TimeZone.UTC
                    }
                    State.Populated(
                        location = location,
                        currentTime = currentTime,
                        sunriseTime = upcomingTimes?.sunriseTime,
                        sunsetTime = upcomingTimes?.sunsetTime,
                        moonriseTime = upcomingTimes?.moonriseTime,
                        moonsetTime = upcomingTimes?.moonsetTime,
                        timeZone = timeZone
                    )
                }
            } else {
                flowOf(State.InvalidLocation)
            }
        }.onEach {
            emitState(it)
        }.launchIn(coroutineScope)
    }

    override suspend fun performAction(action: Action) = when (action) {
        is Action.SetLabel -> setLabel(action.label)
        is Action.ToggleSelected -> toggleSelected()
        is Action.Delete -> delete()
    }

    private suspend fun setLabel(label: String) {
        locationRepository.updateLocationLabel(locationId, label)
    }

    private suspend fun toggleSelected() {
        locationRepository.toggleSelectedLocation(locationId)
    }

    private suspend fun delete() {
        locationRepository.deleteLocation(locationId)
        emitEvent(Event.Exit)
    }

    sealed class State {
        object Loading : State()
        object InvalidLocation : State()
        data class Populated(
            val location: SelectableLocation,
            val currentTime: Instant,
            val sunriseTime: Instant?,
            val sunsetTime: Instant?,
            val moonriseTime: Instant?,
            val moonsetTime: Instant?,
            val timeZone: TimeZone
        ) : State()
    }

    sealed class Event {
        object Exit : Event()
    }

    sealed class Action {
        data class SetLabel(val label: String) : Action()
        object ToggleSelected : Action()
        object Delete : Action()
    }

}
