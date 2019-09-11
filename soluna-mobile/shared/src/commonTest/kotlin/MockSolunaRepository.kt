package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary

class MockSolunaRepository(
    locations: List<Location> = mutableListOf(),
    geocodeMap: Map<String, GeocodeData> = mutableMapOf()
) : SolunaRepository {
    private var nextId = 0L
    private val locations = locations.toMutableList()
    private val geocodeMap = geocodeMap.toMutableMap()

    override suspend fun getLocations(): List<LocationSummary> = locations.map { LocationSummary.Impl(it.id, it.label) }

    override suspend fun getLocation(id: Long): Location? = locations.find { it.id == id }

    override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) {
        locations.add(Location.Impl(nextId++, label, latitude, longitude, timeZone))
    }

    override suspend fun deleteLocation(id: Long) {
        locations.removeAll { it.id == id }
    }

    override suspend fun updateLocationLabel(id: Long, label: String) {
        val index = locations.indexOfFirst { it.id == id }
        if (index < 0) return

        val prevLocation = locations[index]
        locations[index] =
            Location.Impl(id, label, prevLocation.latitude, prevLocation.longitude, prevLocation.timeZone)
    }

    override suspend fun geocodeLocation(location: String): GeocodeData? = geocodeMap[location]
}
