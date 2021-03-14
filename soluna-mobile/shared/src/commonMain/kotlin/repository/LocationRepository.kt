package com.russhwolf.soluna.mobile.repository

import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

interface LocationRepository {

    fun getLocations(): Flow<List<LocationSummary>>

    fun getLocation(id: Long): Flow<Location?>

    suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String)

    suspend fun deleteLocation(id: Long)

    suspend fun updateLocationLabel(id: Long, label: String)

    fun getSelectedLocation(): Flow<Location?>

    suspend fun setSelectedLocation(location: Location?)

    class Impl(
        private val database: SolunaDb,
        private val settings: FlowSettings,
        private val backgroundDispatcher: CoroutineDispatcher
    ) : LocationRepository {

        companion object {
            private const val KEY_SELECTED_LOCATION_ID = "selected_location_id"
        }

        override fun getLocations(): Flow<List<LocationSummary>> =
            database.locationQueries
                .selectAllLocations()
                .asFlow()
                .mapToList(backgroundDispatcher)
                .distinctUntilChanged()

        override fun getLocation(id: Long): Flow<Location?> =
            database.locationQueries
                .selectLocationById(id)
                .asFlow()
                .mapToOneOrNull(backgroundDispatcher)
                .distinctUntilChanged()

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

        override fun getSelectedLocation(): Flow<Location?> =
            settings.getLongOrNullFlow(KEY_SELECTED_LOCATION_ID)
                .flatMapLatest {
                    if (it != null) {
                        getLocation(it)
                    } else {
                        flowOf(null)
                    }
                }

        override suspend fun setSelectedLocation(location: Location?) {
            if (location != null) {
                settings.putLong(KEY_SELECTED_LOCATION_ID, location.id)
            } else {
                settings.remove(KEY_SELECTED_LOCATION_ID)
            }
        }
    }
}
