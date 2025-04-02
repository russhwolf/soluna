package com.russhwolf.soluna.mobile

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb
import com.russhwolf.soluna.mobile.ui.App
import com.russhwolf.soluna.mobile.ui.Dependencies
import com.russhwolf.soluna.mobile.ui.PlatformDependencies
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File

@Preview
@Composable
fun demo() {
    val context = LocalContext.current
    val platform = object : PlatformDependencies {
        override val driver: SqlDriver = AndroidSqliteDriver(SolunaDb.Schema, context)
        override val settings: FlowSettings =
            DataStoreSettings(PreferenceDataStoreFactory.create { File(context.filesDir, "settings.preferences_pb") })
    }
    val dependencies = Dependencies(platform)

    runBlocking {
        dependencies.run {
            var locations = locationRepository.getLocations().first()
            if (locations.isEmpty()) {
                locationRepository.addLocation("Somerville, MA", 42.388, -71.100, TimeZone.of("America/New_York"))
                locationRepository.addLocation("Kotlin Island", 60.002, 29.678, TimeZone.of("Europe/Moscow"))
                locations = locationRepository.getLocations().first()
            }
            val aLocation = locations.find { it.selected }
            if (aLocation == null) {
                locationRepository.toggleSelectedLocation(locations.first().id)
            }
        }
    }

    App(dependencies)
}
