package com.russhwolf.soluna.mobile.screen.home

import app.cash.turbine.test
import com.russhwolf.settings.MockSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.AstronomicalData
import com.russhwolf.soluna.mobile.repository.AstronomicalDataRepository
import com.russhwolf.soluna.mobile.repository.FakeCurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.UpcomingTimesRepository
import com.russhwolf.soluna.mobile.repository.configureMockLocationData
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
import kotlin.time.Duration

class HomeViewModelTest {
    private var locations: Array<Location> = emptyArray()

    private val initialTime: Instant = LocalDateTime(2021, 1, 1, 11, 0).toInstant(TimeZone.UTC)

    private val driver = createInMemorySqlDriver()
    private val database = createDatabase(driver)
    private val locationRepository by lazy {
        database.configureMockLocationData(*locations)
        LocationRepository.Impl(database, settings, Dispatchers.Unconfined)
    }
    private val settings = MockSettings().toFlowSettings(Dispatchers.Unconfined)
    private val astronomicalDataRepository = object : AstronomicalDataRepository {
        override fun getTimes(date: LocalDate, zone: TimeZone, latitude: Double, longitude: Double) =
            AstronomicalData(
                sunriseTime = date.atTime(6, date.dayOfMonth).toInstant(zone),
                sunsetTime = date.atTime(18, date.dayOfMonth).toInstant(zone),
                moonriseTime = date.atTime(20, date.dayOfMonth).toInstant(zone),
                moonsetTime = date.atTime(8, date.dayOfMonth).toInstant(zone)
            )
    }

    private val clockRepository = FakeCurrentTimeRepository(initialTime, emitImmediately = false)
    private val upcomingTimesRepository: UpcomingTimesRepository by lazy {
        UpcomingTimesRepository.Impl(astronomicalDataRepository, clockRepository)
    }

    private val viewModel by lazy {
        HomeViewModel(locationRepository, upcomingTimesRepository, clockRepository, Dispatchers.Unconfined)
            .also { it.activate() }
    }

    @Test
    fun initialState_empty() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(HomeViewModel.State.NoLocationSelected, expectViewModelState())
        }
    }

    @Test
    fun initialState_populated() = suspendTest {
        locations = arrayOf(Location(1, "Home", 27.18, 62.83, "UTC"))
        locationRepository.toggleSelectedLocation(locations[0].id)

        viewModel.stateAndEvents.test {
            assertEquals(HomeViewModel.State.Loading, expectViewModelState())
            expectNoEvents()

            clockRepository.tick()
            assertEquals(
                HomeViewModel.State.Populated(
                    locationName = "Home",
                    currentTime = initialTime,
                    sunriseTime = LocalDate(2021, 1, 2).atTime(6, 2).toInstant(TimeZone.UTC),
                    sunsetTime = LocalDate(2021, 1, 1).atTime(18, 1).toInstant(TimeZone.UTC),
                    moonriseTime = LocalDate(2021, 1, 1).atTime(20, 1).toInstant(TimeZone.UTC),
                    moonsetTime = LocalDate(2021, 1, 2).atTime(8, 2).toInstant(TimeZone.UTC),
                    timeZone = TimeZone.of("UTC")
                ),
                expectViewModelState()
            )
            expectNoEvents()

            clockRepository.tick(Duration.minutes(1))
            assertEquals(
                HomeViewModel.State.Populated(
                    locationName = "Home",
                    currentTime = LocalDateTime(2021, 1, 1, 11, 1).toInstant(TimeZone.UTC),
                    sunriseTime = LocalDate(2021, 1, 2).atTime(6, 2).toInstant(TimeZone.UTC),
                    sunsetTime = LocalDate(2021, 1, 1).atTime(18, 1).toInstant(TimeZone.UTC),
                    moonriseTime = LocalDate(2021, 1, 1).atTime(20, 1).toInstant(TimeZone.UTC),
                    moonsetTime = LocalDate(2021, 1, 2).atTime(8, 2).toInstant(TimeZone.UTC),
                    timeZone = TimeZone.of("UTC")
                ),
                expectViewModelState()
            )
            expectNoEvents()

            clockRepository.tick(Duration.minutes(1))
            assertEquals(
                HomeViewModel.State.Populated(
                    locationName = "Home",
                    currentTime = LocalDateTime(2021, 1, 1, 11, 2).toInstant(TimeZone.UTC),
                    sunriseTime = LocalDate(2021, 1, 2).atTime(6, 2).toInstant(TimeZone.UTC),
                    sunsetTime = LocalDate(2021, 1, 1).atTime(18, 1).toInstant(TimeZone.UTC),
                    moonriseTime = LocalDate(2021, 1, 1).atTime(20, 1).toInstant(TimeZone.UTC),
                    moonsetTime = LocalDate(2021, 1, 2).atTime(8, 2).toInstant(TimeZone.UTC),
                    timeZone = TimeZone.of("UTC")
                ),
                expectViewModelState()
            )
        }
    }

    @Test
    fun navigate_locationList() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(HomeViewModel.State.NoLocationSelected, expectViewModelState())
            expectNoEvents()

            viewModel.performAction(HomeViewModel.Action.Locations)
            assertEquals(HomeViewModel.Event.Locations, expectViewModelEvent())
        }
    }

    @Test
    fun navigate_reminderList() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(HomeViewModel.State.NoLocationSelected, expectViewModelState())
            expectNoEvents()

            viewModel.performAction(HomeViewModel.Action.Reminders)
            assertEquals(HomeViewModel.Event.Reminders, expectViewModelEvent())
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
