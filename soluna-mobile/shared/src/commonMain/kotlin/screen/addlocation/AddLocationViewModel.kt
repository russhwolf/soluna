package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.repository.DeviceLocationResult
import com.russhwolf.soluna.mobile.repository.DeviceLocationService
import com.russhwolf.soluna.mobile.repository.GeocodeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.datetime.TimeZone

class AddLocationViewModel(
    private val locationRepository: LocationRepository,
    private val geocodeRepository: GeocodeRepository,
    private val deviceLocationService: DeviceLocationService,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<AddLocationViewModel.State, AddLocationViewModel.Event, AddLocationViewModel.Action>(
    State(),
    dispatcher
) {
    override fun activate() {}

    override suspend fun performAction(action: Action) = when (action) {
        is Action.CreateLocation -> addLocation(action.label, action.latitude, action.longitude, action.timeZone)
        is Action.GeocodeLocation -> geocodeLocation(action.location)
        Action.DeviceLocation -> getDeviceLocation()
    }

    private suspend fun addLocation(label: String, latitude: String, longitude: String, timeZone: String) {
        val latitudeDouble = latitude.toDoubleOrNull()
        val longitudeDouble = longitude.toDoubleOrNull()

        // Emit both errors if relevant before exiting
        when {
            latitudeDouble == null && longitudeDouble == null ->
                emitState(state.value.copy(latitudeFormatError = true, longitudeFormatError = true))
            latitudeDouble == null -> emitState(state.value.copy(latitudeFormatError = true))
            longitudeDouble == null -> emitState(state.value.copy(longitudeFormatError = true))
        }
        latitudeDouble ?: return
        longitudeDouble ?: return

        emitState(state.value.copy(latitudeFormatError = false, longitudeFormatError = false))
        locationRepository.addLocation(label, latitudeDouble, longitudeDouble, timeZone)
        emitEvent(Event.Exit)
    }

    private suspend fun geocodeLocation(location: String) {
        val geocodeData = geocodeRepository.geocodeLocation(location)
        if (geocodeData != null) {
            emitState(state.value.copy(geocodeError = false))
            emitEvent(
                Event.ShowGeocodeData(
                    latitude = geocodeData.latitude,
                    longitude = geocodeData.longitude,
                    timeZone = geocodeData.timeZone
                )
            )
        } else {
            emitState(state.value.copy(geocodeError = true))
        }
    }

    private suspend fun getDeviceLocation() {
        println("get device location...")
        when (val result = deviceLocationService.getCurrentDeviceLocation()) {
            // TODO errors
            DeviceLocationResult.Incapable,
            DeviceLocationResult.PermissionDenied,
            DeviceLocationResult.Unavailable,
            DeviceLocationResult.RequestFailed -> {
                println("error! $result")
                emitState(state.value.copy(deviceLocationError = result))
            }
            is DeviceLocationResult.Success -> {
                println("success!")
                emitState(state.value.copy(deviceLocationError = null))
                emitEvent(
                    Event.ShowGeocodeData(
                        latitude = result.latitude,
                        longitude = result.longitude,
                        timeZone = TimeZone.currentSystemDefault().id
                    )
                )
            }
        }
    }

    data class State(
        val latitudeFormatError: Boolean = false,
        val longitudeFormatError: Boolean = false,
        val geocodeError: Boolean = false,
        val deviceLocationError: DeviceLocationResult? = null
    )

    sealed class Event {
        data class ShowGeocodeData(
            val latitude: Double,
            val longitude: Double,
            val timeZone: String
        ) : Event()

        object Exit : Event()
    }

    sealed class Action {
        data class CreateLocation(
            val label: String,
            val latitude: String,
            val longitude: String,
            val timeZone: String
        ) : Action()

        data class GeocodeLocation(
            val location: String
        ) : Action()

        object DeviceLocation : Action()
    }
}

