package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.SelectAllLocations
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.util.runInBackground

interface SolunaRepository {
    suspend fun getLocations(): List<LocationSummary>

    suspend fun getLocation(label: String): Location?

    suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String)

    class Impl(private val database: SolunaDb) : SolunaRepository {
        override suspend fun getLocations(): List<LocationSummary> = runInBackground {
            database.locationQueries
                .selectAllLocations()
                .executeAsList()
        }

        override suspend fun getLocation(label: String): Location? = runInBackground {
            database.locationQueries
                .selectLocationByLabel(label)
                .executeAsOneOrNull()
        }

        override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) =
            runInBackground {
                database.locationQueries
                    .insertLocation(label, latitude, longitude, timeZone)
            }
    }
}

// Rename/repackage SqlDelight models as repository-level abstractions
typealias LocationSummary = SelectAllLocations

fun LocationSummary(id: Long, label: String): LocationSummary = SelectAllLocations.Impl(id, label)

typealias Location = com.russhwolf.soluna.mobile.db.Location

fun Location(id: Long, label: String, latitude: Double, longitude: Double, timeZone: String): Location =
    com.russhwolf.soluna.mobile.db.Location.Impl(id, label, latitude, longitude, timeZone)
