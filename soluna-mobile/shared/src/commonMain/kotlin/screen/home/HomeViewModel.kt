package com.russhwolf.soluna.mobile.screen.home

import com.russhwolf.soluna.mobile.repository.CurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
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

class HomeViewModel(
    private val locationRepository: LocationRepository,
    private val upcomingTimesRepository: UpcomingTimesRepository,
    private val currentTimeRepository: CurrentTimeRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<HomeViewModel.State, HomeViewModel.Event, HomeViewModel.Action>(
    State.Loading,
    dispatcher
) {
    override fun activate() {
        locationRepository.getSelectedLocation().flatMapLatest { selectedLocation ->
            if (selectedLocation != null) {
                combine(
                    flowOf(selectedLocation),
                    upcomingTimesRepository.getUpcomingTimes(selectedLocation),
                    currentTimeRepository.getCurrentTimeFlow(Duration.seconds(1))
                ) { location, upcomingTimes, instant ->
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
                }
            } else {
                flowOf(State.NoLocationSelected)
            }
        }.onEach {
            emitState(it)
        }.launchIn(coroutineScope)
    }

    override suspend fun performAction(action: Action) = when (action) {
        Action.Settings -> navigateToSettings()
    }

    private suspend fun navigateToSettings() {
        emitEvent(Event.Settings)
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
        object Settings : Event()
    }

    sealed class Action {
        object Settings : Action()
    }
}
