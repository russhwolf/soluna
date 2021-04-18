package com.russhwolf.soluna.android.ui.screen

import android.os.Bundle
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import org.koin.core.parameter.parametersOf

object Destination {
    const val Home = "home"
    const val LocationList = "locationList"
    const val AddLocation = "addLocation"

    object LocationDetail {
        val template = "locationDetail/{locationId}"
        val arguments = listOf(navArgument("locationId") { type = NavType.LongType })
        fun path(locationId: Long) = "locationDetail/$locationId"
        fun parameters(arguments: Bundle?) = parametersOf(arguments?.getLong("locationId"))
    }
}
