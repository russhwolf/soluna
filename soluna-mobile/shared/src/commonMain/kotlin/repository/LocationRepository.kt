package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface LocationRepository {

    suspend fun getLocations(): List<LocationSummary>

    fun getLocationsFlow(): Flow<List<LocationSummary>>

    suspend fun getLocation(id: Long): Location?

    fun getLocationFlow(id: Long): Flow<Location?>

    suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String)

    suspend fun deleteLocation(id: Long)

    suspend fun updateLocationLabel(id: Long, label: String)

    class Impl(
        private val database: SolunaDb,
        private val backgroundDispatcher: CoroutineDispatcher
    ) : LocationRepository {

        override suspend fun getLocations(): List<LocationSummary> = database.getLocations()

        private suspend fun SolunaDb.getLocations(): List<LocationSummary> = withContext(backgroundDispatcher) {
            locationQueries
                .selectAllLocations()
                .executeAsList()
        }

        override fun getLocationsFlow(): Flow<List<LocationSummary>> =
            database.locationQueries
                .selectAllLocations()
                .asFlow()
                .mapToList(backgroundDispatcher)

        override suspend fun getLocation(id: Long): Location? = database.getLocation(id)

        private suspend fun SolunaDb.getLocation(id: Long): Location? = withContext(backgroundDispatcher) {
            locationQueries
                .selectLocationById(id)
                .executeAsOneOrNull()
        }

        override fun getLocationFlow(id: Long): Flow<Location?> =
            database.locationQueries
                .selectLocationById(id)
                .asFlow()
                .mapToOneOrNull(backgroundDispatcher)

        override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) =
            database.addLocation(label, latitude, longitude, timeZone)

        private suspend fun SolunaDb.addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) =
            withContext(backgroundDispatcher) {
                locationQueries
                    .insertLocation(label, latitude, longitude, timeZone)
            }


        override suspend fun deleteLocation(id: Long) = database.deleteLocation(id)

        private suspend fun SolunaDb.deleteLocation(id: Long) = withContext(backgroundDispatcher) {
            locationQueries
                .deleteLocationById(id)
        }

        override suspend fun updateLocationLabel(id: Long, label: String) = database.updateLocationLabel(id, label)

        private suspend fun SolunaDb.updateLocationLabel(id: Long, label: String) = withContext(backgroundDispatcher) {
            locationQueries
                .updateLocationLabelById(label, id)
        }
    }
}
