package com.russhwolf.soluna.mobile.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class AndroidDeviceLocationService(
    private val context: Context,
    private val locationPermissionRequester: LocationPermissionRequester
) : DeviceLocationService {
    private val locationManager: LocationManager = context.getSystemService()!!
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val settingsClient: SettingsClient by lazy { LocationServices.getSettingsClient(context) }

    private val cancellationTokenSource = CancellationTokenSource()

    private val hasLocationPermission: Boolean
        get() = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private val isLocationEnabled: Boolean
        get() = LocationManagerCompat.isLocationEnabled(locationManager)


    override suspend fun isDeviceLocationCapable(): Boolean =
        settingsClient.checkLocationSettings(
            LocationSettingsRequest.Builder().setAlwaysShow(true).setNeedBle(false).build()
        ).await().locationSettingsStates?.isLocationPresent == true

    @SuppressLint("MissingPermission") // We do permission check before calling unsafe method
    override suspend fun getCurrentDeviceLocation(): DeviceLocationResult {
        val hasLocationPermission = hasLocationPermission || locationPermissionRequester.requestLocationPermission()
        if (!hasLocationPermission) {
            return DeviceLocationResult.PermissionDenied
        }
        if (!isLocationEnabled) {
            return DeviceLocationResult.Unavailable
        }
        return getCurrentDeviceLocationUnsafe()
    }

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
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

interface LocationPermissionRequester {
    suspend fun requestLocationPermission(): Boolean
}

