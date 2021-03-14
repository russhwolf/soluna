package com.russhwolf.soluna.mobile.screen.locationlist

import app.cash.turbine.test
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.configureMockLocationData
import com.russhwolf.soluna.mobile.screen.expectViewModelEvent
import com.russhwolf.soluna.mobile.screen.expectViewModelState
import com.russhwolf.soluna.mobile.screen.stateAndEvents
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationListViewModelTest {
    private var locations: Array<Location> = emptyArray()

    private val driver = createInMemorySqlDriver()
    private val locationRepository by lazy {
        val database = createDatabase(driver)
        database.configureMockLocationData(*locations)
        LocationRepository.Impl(database, Dispatchers.Unconfined)
    }

    private val viewModel by lazy {
        LocationListViewModel(locationRepository, Dispatchers.Unconfined)
    }

    @Test
    fun initialState_empty() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(LocationListViewModel.State(emptyList()), expectViewModelState())
        }
    }

    @Test
    fun initialState_populated() = suspendTest {
        locations = arrayOf(Location(1, "Home", 27.18, 62.83, "UTC"))
        viewModel.stateAndEvents.test {
            assertEquals(LocationListViewModel.State(listOf(LocationSummary(1, "Home"))), expectViewModelState())
        }
    }

    @Test
    fun locationsUpdate() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(LocationListViewModel.State(emptyList()), expectViewModelState())
            expectNoEvents()

            locationRepository.addLocation("Home", 27.18, 62.83, "UTC")
            assertEquals(LocationListViewModel.State(listOf(LocationSummary(1, "Home"))), expectViewModelState())
        }
    }

    @Test
    fun removeLocation() = suspendTest {
        locations = arrayOf(Location(1, "Home", 27.18, 62.83, "UTC"))

        viewModel.stateAndEvents.test {
            assertEquals(LocationListViewModel.State(listOf(LocationSummary(1, "Home"))), expectViewModelState())
            expectNoEvents()

            viewModel.performAction(LocationListViewModel.Action.RemoveLocation(1))
            assertEquals(LocationListViewModel.State(emptyList()), expectViewModelState())
        }
    }

    @Test
    fun addLocationTrigger() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(LocationListViewModel.State(emptyList()), expectViewModelState())
            expectNoEvents()

            viewModel.performAction(LocationListViewModel.Action.AddLocation)
            assertEquals(LocationListViewModel.Event.AddLocation, expectViewModelEvent())
        }
    }

    @Test
    fun locationDetailsTrigger() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(LocationListViewModel.State(emptyList()), expectViewModelState())
            expectNoEvents()

            viewModel.performAction(LocationListViewModel.Action.LocationDetails(LocationSummary(1, "Home")))
            assertEquals(LocationListViewModel.Event.LocationDetails(1), expectViewModelEvent())
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
