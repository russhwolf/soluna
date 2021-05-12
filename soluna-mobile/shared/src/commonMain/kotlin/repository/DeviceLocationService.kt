package com.russhwolf.soluna.mobile.repository

interface DeviceLocationService {
    suspend fun isDeviceLocationCapable(): Boolean

    suspend fun getCurrentDeviceLocation(): DeviceLocationResult
}

sealed class DeviceLocationResult {
    /**
     * The device has no GPS capability. Future requests will never succeed.
     */
    object Incapable : DeviceLocationResult()

    /**
     * GPS permission was requested and then denied. Permission must be enabled for future request to succeed
     */
    object PermissionDenied : DeviceLocationResult()

    /**
     * GPS is unavailable. User must enable it for future request to succeed.
     */
    object Unavailable : DeviceLocationResult()

    /**
     * The GPS request failed for a transient/unknown reason. Future requests may succeed
     */
    object RequestFailed : DeviceLocationResult()

    /**
     * Result of a successful location request
     */
    data class Success(val latitude: Double, val longitude: Double) : DeviceLocationResult()
}
