package com.russhwolf.soluna.mobile.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.cash.sqldelight.db.SqlDriver
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.soluna.mobile.db.invoke
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb
import com.russhwolf.soluna.mobile.repository.AstronomicalTimesRepository
import com.russhwolf.soluna.mobile.repository.CurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.theme.SolunaTheme
import com.russhwolf.soluna.time.InstantAstronomicalCalculator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

// TODO DI
interface PlatformDependencies {
    val driver: SqlDriver
    val settings: FlowSettings
}

class Dependencies(
    val platform: PlatformDependencies,
    val database: SolunaDb = SolunaDb(platform.driver),
    val backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default,
    val locationRepository: LocationRepository = LocationRepository(database, platform.settings, backgroundDispatcher),
    val clock: Clock = Clock.System,
    val currentTimeRepository: CurrentTimeRepository = CurrentTimeRepository(clock),
    val astronomicalTimesRepository: AstronomicalTimesRepository =
        AstronomicalTimesRepository(currentTimeRepository, InstantAstronomicalCalculator::factory)
)

@Composable
fun App(dependencies: Dependencies) {
    val navController = rememberNavController()

    SolunaTheme {
        Surface {
            NavHost(
                navController,
                startDestination = HomeScreen
            ) {
                composable<HomeScreen> {
                    val events = rememberEventsFlow<HomeScreenEvent>()
                    HomeScreen(
                        homeScreenState(
                            dependencies.locationRepository,
                            dependencies.currentTimeRepository,
                            dependencies.astronomicalTimesRepository,
                            events
                        ),
                        onEvent = { events.tryEmit(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> rememberEventsFlow() = remember {
    MutableSharedFlow<T>(
        extraBufferCapacity = 20,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
}

@Serializable
object HomeScreen
