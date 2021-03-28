package com.russhwolf.soluna.mobile.screen.home

import com.russhwolf.soluna.mobile.repository.ClockRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.UpcomingTimesRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant
import kotlin.time.seconds

class HomeViewModel(
    private val locationRepository: LocationRepository,
    private val upcomingTimesRepository: UpcomingTimesRepository,
    private val clockRepository: ClockRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<HomeViewModel.State, HomeViewModel.Event, HomeViewModel.Action>(
    State.Loading,
    dispatcher
) {
    init {
        combine(
            locationRepository.getSelectedLocation(),
            upcomingTimesRepository.getUpcomingTimes(),
            clockRepository.getCurrentTimeFlow(0.1.seconds)
        ) { location, upcomingTimes, instant ->
            if (location != null) {
                State.Populated(
                    locationName = location.label,
                    currentTime = instant,
                    sunriseTime = upcomingTimes?.sunriseTime,
                    sunsetTime = upcomingTimes?.sunsetTime,
                    moonriseTime = upcomingTimes?.moonriseTime,
                    moonsetTime = upcomingTimes?.moonsetTime
                )
            } else {
                State.NoLocationSelected
            }
        }.onEach {
            emitState(it)
        }.launchIn(coroutineScope)
    }

    override suspend fun performAction(action: Action) = when (action) {
        Action.Locations -> navigateToLocationList()
    }

    private suspend fun navigateToLocationList() {
        emitEvent(Event.Locations)
    }


    sealed class State {
        object Loading : State()
        object NoLocationSelected : State()
        data class Populated(
            val locationName: String,
            val currentTime: Instant,
            val sunriseTime: Instant?,
            val sunsetTime: Instant?,
            val moonriseTime: Instant?,
            val moonsetTime: Instant?
        ) : State()
    }

    sealed class Event {
        object Locations : Event()
//        object Reminders : Event()
    }

    sealed class Action {
        object Locations : Action()
//        object Reminders : Action()
    }
}
