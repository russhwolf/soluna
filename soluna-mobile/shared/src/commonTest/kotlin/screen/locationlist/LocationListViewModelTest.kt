package com.russhwolf.soluna.mobile.screen.locationlist

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository.Impl.Companion.KEY_SELECTED_LOCATION_ID
import com.russhwolf.soluna.mobile.repository.SelectableLocationSummary
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
    private val settings = MapSettings().toFlowSettings(Dispatchers.Unconfined)
    private val locationRepository by lazy {
        val database = createDatabase(driver)
        database.configureMockLocationData(*locations)
        LocationRepository.Impl(database, settings, Dispatchers.Unconfined)
    }

    private val viewModel by lazy {
        LocationListViewModel(locationRepository, Dispatchers.Unconfined)
            .also { it.activate() }
    }

    @Test
    fun initialState_empty() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(LocationListViewModel.State(emptyList()), expectViewModelState())
        }
    }

    @Test
    fun initialState_populated() = suspendTest {
        locations = arrayOf(
            Location(1, "Home", 27.18, 62.83, "UTC"),
            Location(2, "Away", 27.18, 62.83, "UTC")
        )
        settings.putLong(KEY_SELECTED_LOCATION_ID, 1)
        viewModel.stateAndEvents.test {
            assertEquals(
                LocationListViewModel.State(
                    listOf(
                        SelectableLocationSummary(2, "Away", false),
                        SelectableLocationSummary(1, "Home", true)
                    )
                ),
                expectViewModelState()
            )
        }
    }

    @Test
    fun locationsUpdate() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(LocationListViewModel.State(emptyList()), expectViewModelState())
            expectNoEvents()

            locationRepository.addLocation("Home", 27.18, 62.83, "UTC")
            assertEquals(
                LocationListViewModel.State(listOf(SelectableLocationSummary(1, "Home", false))),
                expectViewModelState()
            )
        }
    }

    @Test
    fun removeLocation() = suspendTest {
        locations = arrayOf(Location(1, "Home", 27.18, 62.83, "UTC"))

        viewModel.stateAndEvents.test {
            assertEquals(
                LocationListViewModel.State(listOf(SelectableLocationSummary(1, "Home", false))),
                expectViewModelState()
            )
            expectNoEvents()

            viewModel.performAction(LocationListViewModel.Action.RemoveLocation(1))
            assertEquals(LocationListViewModel.State(emptyList()), expectViewModelState())
        }
    }

    @Test
    fun toggleLocationSelected() = suspendTest {
        locations = arrayOf(
            Location(1, "Home", 27.18, 62.83, "UTC"),
            Location(2, "Away", 27.18, 62.83, "UTC")
        )
        settings.putLong(KEY_SELECTED_LOCATION_ID, 1)
        viewModel.stateAndEvents.test {
            assertEquals(
                LocationListViewModel.State(
                    listOf(
                        SelectableLocationSummary(2, "Away", false),
                        SelectableLocationSummary(1, "Home", true)
                    )
                ),
                expectViewModelState()
            )

            viewModel.performAction(LocationListViewModel.Action.ToggleLocationSelected(2))
            assertEquals(
                LocationListViewModel.State(
                    listOf(
                        SelectableLocationSummary(2, "Away", true),
                        SelectableLocationSummary(1, "Home", false)
                    )
                ),
                expectViewModelState()
            )
            expectNoEvents()

            viewModel.performAction(LocationListViewModel.Action.ToggleLocationSelected(2))
            assertEquals(
                LocationListViewModel.State(
                    listOf(
                        SelectableLocationSummary(2, "Away", false),
                        SelectableLocationSummary(1, "Home", false)
                    )
                ),
                expectViewModelState()
            )
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

            viewModel.performAction(LocationListViewModel.Action.LocationDetails(1))
            assertEquals(LocationListViewModel.Event.LocationDetails(1), expectViewModelEvent())
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
