package com.russhwolf.soluna.mobile

class MockSolunaRepository(locations: List<LocationDetail> = mutableListOf()) : SolunaRepository {
    private var nextId = 0L
    private val locations = locations.toMutableList()

    override suspend fun getLocations(): List<LocationSummary> = locations.map { LocationSummary(it.id, it.label) }

    override suspend fun getLocation(id: Long): LocationDetail? = locations.find { it.id == id }

    override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) {
        locations.add(LocationDetail(nextId++, label, latitude, longitude, timeZone))
    }

    override suspend fun deleteLocation(id: Long) {
        locations.removeAll { it.id == id }
    }
}
