package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.GeocodeData
import com.russhwolf.soluna.mobile.GeocodeRepository

class MockGeocodeRepository(geocodeMap: Map<String, GeocodeData> = mutableMapOf()) :
    GeocodeRepository {
    private val geocodeMap = geocodeMap.toMutableMap()

    override suspend fun geocodeLocation(location: String): GeocodeData? = geocodeMap[location]
}
