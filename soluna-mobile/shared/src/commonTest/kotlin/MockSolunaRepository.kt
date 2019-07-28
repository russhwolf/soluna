package com.russhwolf.soluna.mobile

class MockSolunaRepository(locations: List<Location> = mutableListOf()) : SolunaRepository {
    private var nextId = 0L
    private val locations = locations.toMutableList()

    override suspend fun getLocations(): List<LocationSummary> = locations.map { LocationSummary(it.id, it.label) }

    override suspend fun getLocation(label: String): Location? = locations.find { it.label == label }

    override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) {
        locations.add(Location(nextId++, label, latitude, longitude, timeZone))
    }
}
