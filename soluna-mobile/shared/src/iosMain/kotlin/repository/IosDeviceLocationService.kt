package com.russhwolf.soluna.mobile.repository

import kotlinx.atomicfu.atomic
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorized
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.time.Duration

class IosDeviceLocationService : DeviceLocationService {
    private val locationDelegate = LocationDelegate()
    private val locationManager = CLLocationManager().also { it.delegate = locationDelegate }

    private val hasLocationPermission: Boolean
        get() = locationDelegate.latestAuthorizationStatus?.hasLocationPermission == true

    private val isLocationEnabled: Boolean
        get() = CLLocationManager.locationServicesEnabled()

    override suspend fun isDeviceLocationCapable(): Boolean =
        CLLocationManager.significantLocationChangeMonitoringAvailable()

    override suspend fun getCurrentDeviceLocation(): DeviceLocationResult {
        val hasLocationPermission = hasLocationPermission || requestLocationPermission()
        if (!hasLocationPermission) {
            return DeviceLocationResult.PermissionDenied
        }
        if (!isLocationEnabled) {
            return DeviceLocationResult.Unavailable
        }
        return getCurrentDeviceLocationUnsafe()
    }

    private suspend fun requestLocationPermission(): Boolean {
        val deferred = locationDelegate.getAuthorizationAsync()
        locationManager.requestWhenInUseAuthorization()
        return withTimeoutOrNull(Duration.seconds(10)) { deferred.await().hasLocationPermission } ?: false
    }

    private suspend fun getCurrentDeviceLocationUnsafe(): DeviceLocationResult {
        val deferred = locationDelegate.getLocationAsync()
        locationManager.requestLocation()
        return withTimeoutOrNull(Duration.seconds(10)) {
            deferred.await().coordinate.useContents {
                DeviceLocationResult.Success(
                    latitude = latitude,
                    longitude = longitude
                )
            }
        } ?: DeviceLocationResult.RequestFailed
    }
}

private val CLAuthorizationStatus.hasLocationPermission
    get() = this in intArrayOf(
        kCLAuthorizationStatusAuthorizedAlways,
        kCLAuthorizationStatusAuthorizedWhenInUse,
        kCLAuthorizationStatusAuthorized
    )

class LocationDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    var latestAuthorizationStatus: CLAuthorizationStatus? by atomic(null)

    private var authorizationCompletion: CompletableDeferred<CLAuthorizationStatus>? by atomic(null)
    private var locationCompletion: CompletableDeferred<CLLocation>? by atomic(null)

    fun getAuthorizationAsync(): Deferred<CLAuthorizationStatus> {
        authorizationCompletion?.cancel()
        val completion = CompletableDeferred<CLAuthorizationStatus>()
        authorizationCompletion = completion
        return completion
    }

    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
        latestAuthorizationStatus = didChangeAuthorizationStatus

        authorizationCompletion?.complete(didChangeAuthorizationStatus)
        authorizationCompletion = null
    }

    fun getLocationAsync(): Deferred<CLLocation> {
        locationCompletion?.cancel()
        val completion = CompletableDeferred<CLLocation>()
        locationCompletion = completion
        return completion
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        @Suppress("UNCHECKED_CAST")
        didUpdateLocations as List<CLLocation>

        locationCompletion?.complete(didUpdateLocations.last())
        locationCompletion = null
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        // TODO
    }
}
