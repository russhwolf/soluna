package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.util.runInBackground

interface SolunaRepository {
    suspend fun getLocations(): List<LocationSummary>

    suspend fun getLocation(id: Long): LocationDetail?

    suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String)

    suspend fun deleteLocation(id: Long)

    class Impl(private val database: SolunaDb) : SolunaRepository {
        override suspend fun getLocations(): List<LocationSummary> = runInBackground {
            database.locationQueries
                .selectAllLocations(::LocationSummary)
                .executeAsList()
        }

        override suspend fun getLocation(id: Long): LocationDetail? = runInBackground {
            database.locationQueries
                .selectLocationById(id, ::LocationDetail)
                .executeAsOneOrNull()
        }

        override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) =
            runInBackground {
                database.locationQueries
                    .insertLocation(label, latitude, longitude, timeZone)
            }

        override suspend fun deleteLocation(id: Long) = runInBackground {
            database.locationQueries
                .deleteLocationById(id)
        }
    }
}

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
