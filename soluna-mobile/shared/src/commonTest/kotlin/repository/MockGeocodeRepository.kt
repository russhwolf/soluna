package com.russhwolf.soluna.mobile.repository

class MockGeocodeRepository(geocodeMap: Map<String, GeocodeData> = mutableMapOf()) :
    GeocodeRepository {
    private val geocodeMap = geocodeMap.toMutableMap()

    override suspend fun geocodeLocation(location: String): GeocodeData? = geocodeMap[location]
}
