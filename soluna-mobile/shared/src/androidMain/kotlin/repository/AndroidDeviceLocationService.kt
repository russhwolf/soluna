package com.russhwolf.soluna.mobile.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await

private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION

class AndroidDeviceLocationService(
    private val activity: ComponentActivity
) : DeviceLocationService {
    private val locationManager: LocationManager by lazy { activity.getSystemService()!! }
    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            activity
        )
    }
    private val settingsClient: SettingsClient by lazy { LocationServices.getSettingsClient(activity) }

    private val locationPermissionRequester: ActivityResultLauncher<String> by lazy {
        @SuppressLint("InvalidFragmentVersionForActivityResult") // We're not even using fragments so what is this check???
        val requester = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            locationPermissionDeferred?.complete(it)
        }
        requester
    }
    private var locationPermissionDeferred: CompletableDeferred<Boolean>? = null

    private val cancellationTokenSource = CancellationTokenSource()

    private val hasLocationPermission: Boolean
        get() = ContextCompat.checkSelfPermission(activity, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED

    private val isLocationEnabled: Boolean
        get() = LocationManagerCompat.isLocationEnabled(locationManager)


    override suspend fun isDeviceLocationCapable(): Boolean =
        settingsClient.checkLocationSettings(
            LocationSettingsRequest.Builder().setAlwaysShow(true).setNeedBle(false).build()
        ).await().locationSettingsStates?.isLocationPresent == true

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
        locationPermissionDeferred?.cancel()
        locationPermissionDeferred = CompletableDeferred()
        locationPermissionRequester.launch(LOCATION_PERMISSION)
        return locationPermissionDeferred!!.await()
    }

    private suspend fun getCurrentDeviceLocationUnsafe(): DeviceLocationResult {
        val androidLocation = fusedLocationProviderClient.lastLocation.await()
            ?: fusedLocationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).await()
            ?: return DeviceLocationResult.RequestFailed
        return DeviceLocationResult.Success(
            latitude = androidLocation.latitude,
            longitude = androidLocation.longitude
        )
    }
}
