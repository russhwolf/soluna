package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.util.epochSeconds
import com.russhwolf.soluna.mobile.util.runInBackground
import com.squareup.sqldelight.Query
import db.asListFlow
import db.asOneOrNullFlow
import kotlinx.coroutines.flow.Flow

interface SolunaRepository {
    suspend fun getLocations(): List<LocationSummary>

    fun getLocationsFlow(): Flow<List<LocationSummary>>

    suspend fun getLocation(id: Long): Location?

    fun getLocationFlow(id: Long): Flow<Location?>

    suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String)

    suspend fun deleteLocation(id: Long)

    suspend fun updateLocationLabel(id: Long, label: String)

    suspend fun getReminders(): List<ReminderWithLocation>

    fun getRemindersFlow(): Flow<List<ReminderWithLocation>>

    suspend fun getRemindersForLocation(locationId: Long): List<ReminderWithLocation>

    fun getRemindersForLocationFlow(locationId: Long): Flow<List<ReminderWithLocation>>

    suspend fun addReminder(locationId: Long, type: ReminderType, minutesBefore: Int, enabled: Boolean)

    suspend fun deleteReminder(id: Long)

    suspend fun updateReminder(id: Long, minutesBefore: Int? = null, enabled: Boolean? = null)

    suspend fun geocodeLocation(location: String): GeocodeData?

    class Impl(private val database: SolunaDb, private val googleApiClient: GoogleApiClient) : SolunaRepository {

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

        override suspend fun getReminders(): List<ReminderWithLocation> =
            database.getReminders()

        private suspend fun SolunaDb.getReminders(): List<ReminderWithLocation> = runInBackground {
            reminderQueries.selectAllReminders().executeAsList()
        }

        override fun getRemindersFlow(): Flow<List<ReminderWithLocation>> =
            database.reminderQueries
                .selectAllReminders()
                .asListFlow()

        override suspend fun getRemindersForLocation(locationId: Long): List<ReminderWithLocation> =
            database.getRemindersForLocation(locationId)

        private suspend fun SolunaDb.getRemindersForLocation(locationId: Long): List<ReminderWithLocation> =
            runInBackground {
                reminderQueries.selectRemindersByLocationId(locationId).executeAsList()
            }

        override fun getRemindersForLocationFlow(locationId: Long): Flow<List<ReminderWithLocation>> =
            database.reminderQueries
                .selectRemindersByLocationId(locationId)
                .asListFlow()

        override suspend fun addReminder(locationId: Long, type: ReminderType, minutesBefore: Int, enabled: Boolean) =
            database.addReminder(locationId, type, minutesBefore, enabled)

        private suspend fun SolunaDb.addReminder(
            locationId: Long,
            type: ReminderType,
            minutesBefore: Int,
            enabled: Boolean
        ) = runInBackground {
            reminderQueries
                .insertReminder(locationId, type, minutesBefore, enabled)
        }

        override suspend fun deleteReminder(id: Long) = database.deleteReminder(id)

        private suspend fun SolunaDb.deleteReminder(id: Long) = runInBackground {
            reminderQueries
                .deleteReminderById(id)
        }

        override suspend fun updateReminder(id: Long, minutesBefore: Int?, enabled: Boolean?) =
            database.updateReminder(id, minutesBefore, enabled)

        private suspend fun SolunaDb.updateReminder(id: Long, minutesBefore: Int?, enabled: Boolean?) =
            runInBackground {
                transaction {
                    if (minutesBefore != null) {
                        reminderQueries.updateReminderMinutesBeforeById(minutesBefore, id)
                    }
                    if (enabled != null) {
                        reminderQueries.updateReminderEnabledById(enabled, id)
                    }
                }
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
