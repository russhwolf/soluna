package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.SelectAllLocations
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
                .selectAllLocations()
                .executeAsList()
        }

        override suspend fun getLocation(id: Long): LocationDetail? = runInBackground {
            database.locationQueries
                .selectLocationById(id)
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

// Rename/repackage SqlDelight models as repository-level abstractions
typealias LocationSummary = SelectAllLocations

fun LocationSummary(id: Long, label: String): LocationSummary = SelectAllLocations.Impl(id, label)

typealias LocationDetail = com.russhwolf.soluna.mobile.db.Location

fun LocationDetail(id: Long, label: String, latitude: Double, longitude: Double, timeZone: String): LocationDetail =
    com.russhwolf.soluna.mobile.db.Location.Impl(id, label, latitude, longitude, timeZone)
