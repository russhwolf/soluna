package com.russhwolf.soluna.mobile

import android.content.Context
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.db.invoke
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb
import com.russhwolf.soluna.mobile.repository.AstronomicalTimesRepository
import com.russhwolf.soluna.mobile.repository.CurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.theme.SolunaTheme
import com.russhwolf.soluna.mobile.ui.HomeScreen
import com.russhwolf.soluna.mobile.ui.HomeScreenEvent
import com.russhwolf.soluna.mobile.ui.homeScreenState
import com.russhwolf.soluna.time.InstantAstronomicalCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun demo() {
    val context = LocalContext.current
    val driver = AndroidSqliteDriver(SolunaDb.Schema, context)
    val database = SolunaDb(driver)
    val settings =
        SharedPreferencesSettings(context.getSharedPreferences("settings", Context.MODE_PRIVATE)).toFlowSettings()
    val locationRepository = LocationRepository(
        database, settings, Dispatchers.Default
    )
    runBlocking {
        var locations = locationRepository.getLocations().first()
        if (locations.isEmpty()) {
            locationRepository.addLocation("Somerville, MA", 42.388, -71.100, TimeZone.of("America/New_York"))
            locationRepository.addLocation("Kotlin Island", 60.002, 29.678, TimeZone.of("Europe/Moscow"))
            locations = locationRepository.getLocations().first()
        }
        val aLocation = locations.first()
        if (!aLocation.selected) {
            locationRepository.toggleSelectedLocation(aLocation.id)
        }
    }
    val currentTimeRepository = CurrentTimeRepository(Clock.System)
    val astronomicalTimesRepository =
        AstronomicalTimesRepository(currentTimeRepository, InstantAstronomicalCalculator::factory)
    val events = remember {
        MutableSharedFlow<HomeScreenEvent>(
            extraBufferCapacity = 20,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    val state = homeScreenState(
        locationRepository,
        currentTimeRepository,
        astronomicalTimesRepository,
        events
    )

    SolunaTheme {
        Surface {
            HomeScreen(
                state = state,
                onEvent = {
                    val result = events.tryEmit(it)
                    require(result)
                }
            )
        }
    }
}
