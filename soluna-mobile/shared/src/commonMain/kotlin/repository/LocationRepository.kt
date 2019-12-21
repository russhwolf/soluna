package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.asListFlow
import com.russhwolf.soluna.mobile.db.asOneOrNullFlow
import com.russhwolf.soluna.mobile.util.runInBackground
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getLocations(): List<LocationSummary>

    fun getLocationsFlow(): Flow<List<LocationSummary>>

    suspend fun getLocation(id: Long): Location?

    fun getLocationFlow(id: Long): Flow<Location?>

    suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String)

    suspend fun deleteLocation(id: Long)

    suspend fun updateLocationLabel(id: Long, label: String)

    class Impl(private val database: SolunaDb) : LocationRepository {

        override suspend fun getLocations(): List<LocationSummary> = database.getLocations()

        private suspend fun SolunaDb.getLocations(): List<LocationSummary> = runInBackground {
            locationQueries
                .selectAllLocations()
                .executeAsList()
        }

        override fun getLocationsFlow(): Flow<List<LocationSummary>> =
            database.locationQueries
                .selectAllLocations()
                .asListFlow()

        override suspend fun getLocation(id: Long): Location? = database.getLocation(id)

        private suspend fun SolunaDb.getLocation(id: Long): Location? = runInBackground {
            locationQueries
                .selectLocationById(id)
                .executeAsOneOrNull()
        }

        override fun getLocationFlow(id: Long): Flow<Location?> =
            database.locationQueries
                .selectLocationById(id)
                .asOneOrNullFlow()

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
    }
}
