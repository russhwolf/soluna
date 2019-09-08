package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.util.epochSeconds
import com.russhwolf.soluna.mobile.util.runInBackground

interface SolunaRepository {
    suspend fun getLocations(): List<LocationSummary>

    suspend fun getLocation(id: Long): LocationDetail?

    suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String)

    suspend fun deleteLocation(id: Long)

    suspend fun updateLocationLabel(id: Long, label: String)

    suspend fun geocodeLocation(location: String): GeocodeData?

    class Impl(private val database: SolunaDb, private val googleApiClient: GoogleApiClient) : SolunaRepository {

        override suspend fun getLocations(): List<LocationSummary> = database.getLocations()

        private suspend fun SolunaDb.getLocations(): List<LocationSummary> = runInBackground {
            locationQueries
                .selectAllLocations(::LocationSummary)
                .executeAsList()
        }

        override suspend fun getLocation(id: Long): LocationDetail? = database.getLocation(id)

        private suspend fun SolunaDb.getLocation(id: Long): LocationDetail? = runInBackground {
            locationQueries
                .selectLocationById(id, ::LocationDetail)
                .executeAsOneOrNull()
        }

        override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) =
            database.addLocation(label, latitude, longitude, timeZone)

        private suspend fun SolunaDb.addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) =
            runInBackground {
                locationQueries
                    .insertLocation(label, latitude, longitude, timeZone)
            }


        override suspend fun deleteLocation(id: Long) = database.deleteLocation(id)

        private suspend fun SolunaDb.deleteLocation(id: Long) = runInBackground {
            locationQueries
                .deleteLocationById(id)
        }

        override suspend fun updateLocationLabel(id: Long, label: String) = database.updateLocationLabel(id, label)

        private suspend fun SolunaDb.updateLocationLabel(id: Long, label: String) = runInBackground {
            locationQueries
                .updateLocationLabelById(label, id)
        }

        override suspend fun geocodeLocation(location: String): GeocodeData? {
            val placeId =
                googleApiClient.getPlaceAutocomplete(location).predictions.firstOrNull()?.place_id ?: return null
            val coords =
                googleApiClient.getGeocode(placeId).results.firstOrNull()?.geometry?.location ?: return null
            val timeZone =
                googleApiClient.getTimeZone(coords.lat, coords.lng, epochSeconds).timeZoneId
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

// Repackage SqlDelight models as repository-level abstractions
data class LocationSummary(
    val id: Long,
    val label: String
)

data class LocationDetail(
    val id: Long,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val timeZone: String
)
