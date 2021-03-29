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
import kotlinx.datetime.TimeZone
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
                val timeZone = if (location.timeZone in TimeZone.availableZoneIds) {
                    TimeZone.of(location.timeZone)
                } else {
                    // TODO better error handling
                    TimeZone.UTC
                }
                State.Populated(
                    locationName = location.label,
                    currentTime = instant,
                    sunriseTime = upcomingTimes?.sunriseTime,
                    sunsetTime = upcomingTimes?.sunsetTime,
                    moonriseTime = upcomingTimes?.moonriseTime,
                    moonsetTime = upcomingTimes?.moonsetTime,
                    timeZone = timeZone
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
        Action.Reminders -> navigateToReminderList()
    }

    private suspend fun navigateToLocationList() {
        emitEvent(Event.Locations)
    }

    private suspend fun navigateToReminderList() {
        emitEvent(Event.Reminders)
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
            val moonsetTime: Instant?,
            val timeZone: TimeZone
        ) : State()
    }

    sealed class Event {
        object Locations : Event()
        object Reminders : Event()
    }

    sealed class Action {
        object Locations : Action()
        object Reminders : Action()
    }
}