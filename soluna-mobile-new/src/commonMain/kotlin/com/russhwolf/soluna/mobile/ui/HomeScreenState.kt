package com.russhwolf.soluna.mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.mobile.graphics.SunMoonTimesGraphicState
import com.russhwolf.soluna.mobile.graphics.sunMoonTimesGraphicStateDaily
import com.russhwolf.soluna.mobile.graphics.sunMoonTimesGraphicStateNext
import com.russhwolf.soluna.mobile.repository.AstronomicalTimesRepository
import com.russhwolf.soluna.mobile.repository.CurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.SelectableLocation
import com.russhwolf.soluna.mobile.repository.SelectableLocationSummary
import com.russhwolf.soluna.mobile.util.LoadingResource
import com.russhwolf.soluna.mobile.util.getOrNull
import com.russhwolf.soluna.mobile.util.isLoading
import com.russhwolf.soluna.mobile.util.produceLoadingResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

@Composable
fun homeScreenState(
    locationRepository: LocationRepository,
    currentTimeRepository: CurrentTimeRepository,
    astronomicalTimesRepository: AstronomicalTimesRepository,
    events: Flow<HomeScreenEvent>,
): HomeScreenState {

    val displayedLocationId = remember { mutableStateOf<Long?>(null) }
    val locationResourceState = produceLoadingResourceState<SelectableLocation?>(displayedLocationId.value) {
        val locationId = displayedLocationId.value
        val location = if (locationId == null) {
            locationRepository.getSelectedLocation().stateIn(this).value
        } else {
            locationRepository.getLocation(locationId).stateIn(this).value
        }
        value = LoadingResource.Value(location)
    }
    val locationListResourceState = produceLoadingResourceState<List<SelectableLocationSummary>> {
        value = LoadingResource.Value(locationRepository.getLocations().stateIn(this).value)
    }

    // TODO partially loaded states?
    if (locationResourceState.value.isLoading() || locationListResourceState.value.isLoading()) {
        return HomeScreenState.Initial
    }
    val location = locationResourceState.value.getOrNull() ?: return HomeScreenState.NoLocationSelected
    val locationList = locationListResourceState.value.getOrNull() ?: emptyList()

    val datePickerVisibleState = remember { mutableStateOf(false) }
    val locationListVisibleState = remember { mutableStateOf(false) }
    val modeState = remember { mutableStateOf<HomeScreenState.Mode>(HomeScreenState.Mode.Next) }
    LaunchedEffect(Unit) {
        events
            .onEach { event ->
                when (event) {
                    is HomeScreenEvent.DatePickerVisibilityChange -> {
                        datePickerVisibleState.value = event.visible
                    }

                    is HomeScreenEvent.LocationListVisibilityChange -> {
                        locationListVisibleState.value = event.visible
                    }

                    is HomeScreenEvent.ModeChange -> {
                        modeState.value = event.mode
                    }

                    is HomeScreenEvent.DisplayedLocationChange -> {
                        displayedLocationId.value = event.locationId
                    }
                }
            }
            .collect()
    }


    val sunMoonTimesGraphicState = when (val mode = modeState.value) {
        is HomeScreenState.Mode.Daily -> sunMoonTimesGraphicStateDaily(
            location,
            astronomicalTimesRepository,
            mode.date
        )

        is HomeScreenState.Mode.Next -> sunMoonTimesGraphicStateNext(
            location,
            currentTimeRepository,
            astronomicalTimesRepository
        )
    }

    // TODO we compute times twice. Should we pass down to SunMoonTimesGraphicState instead?
    return when (val mode = modeState.value) {
        is HomeScreenState.Mode.Daily -> {
            val astronomicalTimes = astronomicalTimesRepository.getTimesForDate(mode.date, location)

            HomeScreenState.Daily(
                mode.date,
                location.label,
                location.timeZone,
                location.latitude,
                location.longitude,
                astronomicalTimes.sunTimes,
                astronomicalTimes.moonTimes,
                datePickerVisibleState.value,
                locationList,
                locationListVisibleState.value,
                sunMoonTimesGraphicState
            )
        }

        is HomeScreenState.Mode.Next -> {
            val scope = rememberCoroutineScope()
            val astronomicalTimesState = astronomicalTimesRepository.getUpcomingTimes(scope, location).collectAsState()

            HomeScreenState.Next(
                currentTimeRepository.getCurrentTime(),
                location.label,
                location.timeZone,
                location.latitude,
                location.longitude,
                astronomicalTimesState.value.sunTimes,
                astronomicalTimesState.value.moonTimes,
                datePickerVisibleState.value,
                locationList,
                locationListVisibleState.value,
                sunMoonTimesGraphicState
            )
        }
    }
}

sealed interface HomeScreenState {

    data object Initial : HomeScreenState

    data object NoLocationSelected : HomeScreenState

    sealed interface Populated : HomeScreenState {
        val locationName: String
        val timeZone: TimeZone
        val latitude: Double
        val longitude: Double
        val sunTimes: RiseSetResult<Instant>
        val moonTimes: RiseSetResult<Instant>
        val datePickerVisible: Boolean
        val locationList: List<SelectableLocationSummary>
        val locationListVisible: Boolean
        val sunMoonTimesGraphicState: SunMoonTimesGraphicState
    }

    data class Daily(
        val date: LocalDate,
        override val locationName: String,
        override val timeZone: TimeZone,
        override val latitude: Double,
        override val longitude: Double,
        override val sunTimes: RiseSetResult<Instant>,
        override val moonTimes: RiseSetResult<Instant>,
        override val datePickerVisible: Boolean,
        override val locationList: List<SelectableLocationSummary>,
        override val locationListVisible: Boolean,
        override val sunMoonTimesGraphicState: SunMoonTimesGraphicState,
    ) : Populated

    data class Next(
        val currentTime: Instant,
        override val locationName: String,
        override val timeZone: TimeZone,
        override val latitude: Double,
        override val longitude: Double,
        override val sunTimes: RiseSetResult<Instant>,
        override val moonTimes: RiseSetResult<Instant>,
        override val datePickerVisible: Boolean,
        override val locationList: List<SelectableLocationSummary>,
        override val locationListVisible: Boolean,
        override val sunMoonTimesGraphicState: SunMoonTimesGraphicState,
    ) : Populated

    sealed interface Mode {
        data class Daily(val date: LocalDate) : Mode
        data object Next : Mode
    }
}

sealed interface HomeScreenEvent {
    data class DatePickerVisibilityChange(val visible: Boolean) : HomeScreenEvent
    data class LocationListVisibilityChange(val visible: Boolean) : HomeScreenEvent
    data class ModeChange(val mode: HomeScreenState.Mode) : HomeScreenEvent
    data class DisplayedLocationChange(val locationId: Long) : HomeScreenEvent
}
