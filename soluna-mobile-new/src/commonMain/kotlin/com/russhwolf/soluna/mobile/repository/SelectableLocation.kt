package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.sqldelight.Location
import com.russhwolf.soluna.mobile.db.sqldelight.LocationSummary

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
