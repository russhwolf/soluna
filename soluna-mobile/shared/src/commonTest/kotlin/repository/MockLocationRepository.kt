package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.runBlocking
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class MockLocationRepository(vararg locations: Location) : LocationRepository {
    private var nextLocationId = 0L
    private val locations = locations.toMutableList()

    private val locationListeners = mutableListOf<() -> Unit>()

    override suspend fun getLocations(): List<LocationSummary> = locations.map { LocationSummary.Impl(it.id, it.label) }

    override fun getLocationsFlow(): Flow<List<LocationSummary>> = callbackFlow {
        val listener: () -> Unit = { offer(runBlocking { getLocations() }) }

        locationListeners.add(listener)
        awaitClose {
            locationListeners.remove(listener)
        }
    }.distinctUntilChanged()

    override suspend fun getLocation(id: Long): Location? = locations.find { it.id == id }

    override fun getLocationFlow(id: Long): Flow<Location?> = callbackFlow {
        val listener: () -> Unit = { offer(runBlocking { getLocation(id) }) }

        locationListeners.add(listener)
        awaitClose {
            locationListeners.remove(listener)
        }
    }.distinctUntilChanged()

    override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) {
        locations.add(Location.Impl(nextLocationId++, label, latitude, longitude, timeZone))
        locationListeners.forEach { it() }
    }

    override suspend fun deleteLocation(id: Long) {
        locations.removeAll { it.id == id }
        locationListeners.forEach { it() }
    }

    override suspend fun updateLocationLabel(id: Long, label: String) {
        val index = locations.indexOfFirst { it.id == id }
        if (index < 0) return

        val prevLocation = locations[index]
        locations[index] =
            Location.Impl(id, label, prevLocation.latitude, prevLocation.longitude, prevLocation.timeZone)

        locationListeners.forEach { it() }
    }
}
