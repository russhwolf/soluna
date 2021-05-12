package com.russhwolf.soluna.mobile.repository

class FakeDeviceLocationService(
    var isDeviceLocationCapable: Boolean,
    var currentDeviceLocation: DeviceLocationResult
) : DeviceLocationService {
    override suspend fun isDeviceLocationCapable(): Boolean = isDeviceLocationCapable
    override suspend fun getCurrentDeviceLocation(): DeviceLocationResult = currentDeviceLocation
}
