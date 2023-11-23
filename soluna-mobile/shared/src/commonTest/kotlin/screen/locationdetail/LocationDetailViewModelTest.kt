package com.russhwolf.soluna.mobile.screen.locationdetail

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.AstronomicalData
import com.russhwolf.soluna.mobile.repository.AstronomicalDataRepository
import com.russhwolf.soluna.mobile.repository.FakeCurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository.Impl.Companion.KEY_SELECTED_LOCATION_ID
import com.russhwolf.soluna.mobile.repository.UpcomingTimesRepository
import com.russhwolf.soluna.mobile.repository.configureMockLocationData
import com.russhwolf.soluna.mobile.repository.toSelectableLocation
import com.russhwolf.soluna.mobile.screen.expectViewModelEvent
import com.russhwolf.soluna.mobile.screen.expectViewModelState
import com.russhwolf.soluna.mobile.screen.stateAndEvents
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationDetailViewModelTest {
    private var locations: Array<Location> = emptyArray()
    private val currentTime: Instant = LocalDateTime(2021, 1, 1, 11, 0).toInstant(TimeZone.UTC)

    private val driver = createInMemorySqlDriver()
    private val settings = MapSettings().toFlowSettings(Dispatchers.Unconfined)
    private val locationRepository by lazy {
        val database = createDatabase(driver)
        database.configureMockLocationData(*locations)
        LocationRepository.Impl(database, settings, Dispatchers.Unconfined)
    }

    private val today = LocalDate(2021, 1, 1)
    private val timeZone = TimeZone.of("UTC")
    private val now = today.atTime(12, 0).toInstant(timeZone)
    private val sunrise = today.atTime(6, 0).toInstant(timeZone)
    private val sunset = today.atTime(18, 0).toInstant(timeZone)
    private val moonrise = today.atTime(20, 0).toInstant(timeZone)
    private val moonset = today.atTime(8, 0).toInstant(timeZone)

    private val astronomicalDataRepository = object : AstronomicalDataRepository {
        override fun getTimes(date: LocalDate, zone: TimeZone, latitude: Double, longitude: Double) =
            AstronomicalData(sunrise, sunset, moonrise, moonset)
    }
    private val currentTimeRepository = FakeCurrentTimeRepository(currentTime)
    private val upcomingTimesRepository: UpcomingTimesRepository by lazy {
        UpcomingTimesRepository.Impl(astronomicalDataRepository, FakeCurrentTimeRepository(now))
    }

    val viewModel by lazy {
        LocationDetailViewModel(
            1,
            locationRepository,
            upcomingTimesRepository,
            currentTimeRepository,
            Dispatchers.Unconfined
        )
            .also { it.activate() }
    }

    @Test
    fun initialState_empty() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(LocationDetailViewModel.State.InvalidLocation, expectViewModelState())
        }
    }

    @Test
    fun initialState_populated() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        settings.putLong(KEY_SELECTED_LOCATION_ID, 1)

        viewModel.stateAndEvents.test {
            assertEquals(
                LocationDetailViewModel.State.Populated(
                    location.toSelectableLocation(true),
                    currentTime,
                    sunrise,
                    sunset,
                    moonrise,
                    moonset,
                    timeZone
                ),
                expectViewModelState()
            )
        }
    }

    @Test
    fun updateLabel() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)

        viewModel.stateAndEvents.test {
            assertEquals(
                LocationDetailViewModel.State.Populated(
                    location.toSelectableLocation(false),
                    currentTime,
                    sunrise,
                    sunset,
                    moonrise,
                    moonset,
                    timeZone
                ),
                expectViewModelState()
            )
            expectNoEvents()

            viewModel.performAction(LocationDetailViewModel.Action.SetLabel("Updated"))
            assertEquals(
                LocationDetailViewModel.State.Populated(
                    location.copy(label = "Updated").toSelectableLocation(false),
                    currentTime,
                    sunrise,
                    sunset,
                    moonrise,
                    moonset,
                    timeZone
                ),
                expectViewModelState()
            )
        }
    }

    @Test
    fun toggleSelected() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        settings.putLong(KEY_SELECTED_LOCATION_ID, 2)

        viewModel.stateAndEvents.test {
            assertEquals(
                LocationDetailViewModel.State.Populated(
                    location.toSelectableLocation(false),
                    currentTime,
                    sunrise,
                    sunset,
                    moonrise,
                    moonset,
                    timeZone
                ),
                expectViewModelState()
            )
            expectNoEvents()

            viewModel.performAction(LocationDetailViewModel.Action.ToggleSelected)
            assertEquals(
                LocationDetailViewModel.State.Populated(
                    location.toSelectableLocation(true),
                    currentTime,
                    sunrise,
                    sunset,
                    moonrise,
                    moonset,
                    timeZone
                ),
                expectViewModelState()
            )
            expectNoEvents()

            viewModel.performAction(LocationDetailViewModel.Action.ToggleSelected)
            assertEquals(
                LocationDetailViewModel.State.Populated(
                    location.toSelectableLocation(false),
                    currentTime,
                    sunrise,
                    sunset,
                    moonrise,
                    moonset,
                    timeZone
                ),
                expectViewModelState()
            )
        }
    }

    @Test
    fun deleteLocation() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)

        viewModel.stateAndEvents.test {
            assertEquals(
                LocationDetailViewModel.State.Populated(
                    location.toSelectableLocation(false),
                    currentTime,
                    sunrise,
                    sunset,
                    moonrise,
                    moonset,
                    timeZone
                ),
                expectViewModelState()
            )
            expectNoEvents()

            viewModel.performAction(LocationDetailViewModel.Action.Delete)
            assertEquals(LocationDetailViewModel.State.InvalidLocation, expectViewModelState())
            assertEquals(LocationDetailViewModel.Event.Exit, expectViewModelEvent())
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
