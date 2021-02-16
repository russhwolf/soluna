package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.SolunaDb

fun SolunaDb.configureMockLocationData(vararg locations: Location) = transaction {
    locations.forEach { locationQueries.insertLocation(it.label, it.latitude, it.longitude, it.timeZone) }
}
