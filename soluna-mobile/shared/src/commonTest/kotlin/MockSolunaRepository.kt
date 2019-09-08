package com.russhwolf.soluna.mobile

class MockSolunaRepository(
    locations: List<LocationDetail> = mutableListOf(),
    geocodeMap: Map<String, GeocodeData> = mutableMapOf()
) : SolunaRepository {
    private var nextId = 0L
    private val locations = locations.toMutableList()
    private val geocodeMap = geocodeMap.toMutableMap()

    override suspend fun getLocations(): List<LocationSummary> = locations.map { LocationSummary(it.id, it.label) }

    override suspend fun getLocation(id: Long): LocationDetail? = locations.find { it.id == id }

    override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) {
        locations.add(LocationDetail(nextId++, label, latitude, longitude, timeZone))
    }

    override suspend fun deleteLocation(id: Long) {
        locations.removeAll { it.id == id }
    }

    override suspend fun updateLocationLabel(id: Long, label: String) {
        val index = locations.indexOfFirst { it.id == id }
        if (index < 0) return

        locations[index] = locations[index].copy(label = label)
    }

    override suspend fun geocodeLocation(location: String): GeocodeData? = geocodeMap[location]
}
