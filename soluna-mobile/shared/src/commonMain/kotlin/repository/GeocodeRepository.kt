package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.api.GoogleApiClient

interface GeocodeRepository {

    suspend fun geocodeLocation(location: String): GeocodeData?

    class Impl(
        private val googleApiClient: GoogleApiClient,
        private val currentTimeRepository: CurrentTimeRepository
    ) : GeocodeRepository {

        override suspend fun geocodeLocation(location: String): GeocodeData? {
            val placeId = googleApiClient.getPlaceAutocomplete(location)?.predictions?.firstOrNull()?.place_id
                ?: return null
            val coords = googleApiClient.getGeocode(placeId)?.results?.firstOrNull()?.geometry?.location
                ?: return null
            val timeZone = googleApiClient.getTimeZone(
                coords.lat ?: return null,
                coords.lng ?: return null,
                currentTimeRepository.getCurrentTime().toEpochMilliseconds() / 1000
            )?.timeZoneId
                ?: return null
            return GeocodeData(
                latitude = coords.lat,
                longitude = coords.lng,
                timeZone = timeZone
            )
        }
    }
}

data class GeocodeData(
    val latitude: Double,
    val longitude: Double,
    val timeZone: String
)
