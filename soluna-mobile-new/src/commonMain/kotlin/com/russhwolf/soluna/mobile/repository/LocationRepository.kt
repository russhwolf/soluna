package com.russhwolf.soluna.mobile.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class LocationRepository(
    private val database: SolunaDb,
    private val settings: FlowSettings,
    private val backgroundDispatcher: CoroutineDispatcher
) {

    companion object {
        internal const val KEY_SELECTED_LOCATION_ID = "selected_location_id"
    }

    fun getLocations(): Flow<List<SelectableLocationSummary>> =
        database.locationQueries
            .selectAllLocations()
            .asFlow()
            .mapToList(backgroundDispatcher)
            .combine(settings.getLongOrNullFlow(KEY_SELECTED_LOCATION_ID)) { locations, selectedLocationId ->
                locations.map { it.toSelectableLocationSummary(it.id == selectedLocationId) }
            }
            .distinctUntilChanged()
            .flowOn(backgroundDispatcher)

    fun getLocation(id: Long): Flow<SelectableLocation?> =
        database.locationQueries
            .selectLocationById(id)
            .asFlow()
            .mapToOneOrNull(backgroundDispatcher)
            .combine(settings.getLongOrNullFlow(KEY_SELECTED_LOCATION_ID)) { location, selectedLocationId ->
                location?.toSelectableLocation(location.id == selectedLocationId)
            }
            .distinctUntilChanged()
            .flowOn(backgroundDispatcher)

    suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) =
        withContext(backgroundDispatcher) {
            database.locationQueries
                .insertLocation(label, latitude, longitude, timeZone)
        }


    suspend fun deleteLocation(id: Long) = withContext(backgroundDispatcher) {
        database.locationQueries
            .deleteLocationById(id)
    }

    suspend fun updateLocationLabel(id: Long, label: String) = withContext(backgroundDispatcher) {
        database.locationQueries
            .updateLocationLabelById(label, id)
    }

    fun getSelectedLocation(): Flow<SelectableLocation?> =
        settings.getLongOrNullFlow(KEY_SELECTED_LOCATION_ID)
            .flatMapLatest {
                if (it != null) {
                    getLocation(it)
                } else {
                    flowOf(null)
                }
            }

    suspend fun toggleSelectedLocation(locationId: Long) {
        val selectedLocationId = settings.getLongOrNull(KEY_SELECTED_LOCATION_ID)
        if (locationId == selectedLocationId) {
            settings.remove(KEY_SELECTED_LOCATION_ID)
        } else {
            settings.putLong(KEY_SELECTED_LOCATION_ID, locationId)
        }
    }
}

