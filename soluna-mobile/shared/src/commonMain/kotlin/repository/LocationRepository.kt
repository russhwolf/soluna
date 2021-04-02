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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

interface LocationRepository {

    fun getLocations(): Flow<List<SelectableLocationSummary>>

    fun getLocation(id: Long): Flow<SelectableLocation?>

    suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String)

    suspend fun deleteLocation(id: Long)

    suspend fun updateLocationLabel(id: Long, label: String)

    fun getSelectedLocation(): Flow<SelectableLocation?>

    suspend fun setSelectedLocationId(locationId: Long?)

    class Impl(
        private val database: SolunaDb,
        private val settings: FlowSettings,
        private val backgroundDispatcher: CoroutineDispatcher
    ) : LocationRepository {

        companion object {
            internal const val KEY_SELECTED_LOCATION_ID = "selected_location_id"
        }

        override fun getLocations(): Flow<List<SelectableLocationSummary>> =
            database.locationQueries
                .selectAllLocations()
                .asFlow()
                .mapToList(backgroundDispatcher)
                .combine(settings.getLongOrNullFlow(KEY_SELECTED_LOCATION_ID)) { locations, selectedLocationId ->
                    locations.map { it.toSelectableLocationSummary(it.id == selectedLocationId) }
                }
                .distinctUntilChanged()
                .flowOn(backgroundDispatcher)

        override fun getLocation(id: Long): Flow<SelectableLocation?> =
            database.locationQueries
                .selectLocationById(id)
                .asFlow()
                .mapToOneOrNull(backgroundDispatcher)
                .combine(settings.getLongOrNullFlow(KEY_SELECTED_LOCATION_ID)) { location, selectedLocationId ->
                    location?.toSelectableLocation(location.id == selectedLocationId)
                }
                .distinctUntilChanged()
                .flowOn(backgroundDispatcher)

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

        override fun getSelectedLocation(): Flow<SelectableLocation?> =
            settings.getLongOrNullFlow(KEY_SELECTED_LOCATION_ID)
                .flatMapLatest {
                    if (it != null) {
                        getLocation(it)
                    } else {
                        flowOf(null)
                    }
                }

        override suspend fun setSelectedLocationId(locationId: Long?) {
            if (locationId != null) {
                settings.putLong(KEY_SELECTED_LOCATION_ID, locationId)
            } else {
                settings.remove(KEY_SELECTED_LOCATION_ID)
            }
        }
    }
}

data class SelectableLocation(
    val id: Long,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val timeZone: String,
    val selected: Boolean
)

fun Location.toSelectableLocation(selected: Boolean) =
    SelectableLocation(
        id = id,
        label = label,
        latitude = latitude,
        longitude = longitude,
        timeZone = timeZone,
        selected = selected
    )

data class SelectableLocationSummary(
    val id: Long,
    val label: String,
    val selected: Boolean
)

fun LocationSummary.toSelectableLocationSummary(selected: Boolean) =
    SelectableLocationSummary(
        id = id,
        label = label,
        selected = selected
    )
