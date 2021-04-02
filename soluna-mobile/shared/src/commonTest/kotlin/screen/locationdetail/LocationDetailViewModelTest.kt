package com.russhwolf.soluna.mobile.screen.locationdetail

import app.cash.turbine.test
import com.russhwolf.settings.MockSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository.Impl.Companion.KEY_SELECTED_LOCATION_ID
import com.russhwolf.soluna.mobile.repository.configureMockLocationData
import com.russhwolf.soluna.mobile.repository.toSelectableLocation
import com.russhwolf.soluna.mobile.screen.expectViewModelEvent
import com.russhwolf.soluna.mobile.screen.expectViewModelState
import com.russhwolf.soluna.mobile.screen.stateAndEvents
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationDetailViewModelTest {
    private var locations: Array<Location> = emptyArray()
    private val driver = createInMemorySqlDriver()
    private val settings = MockSettings().toFlowSettings(Dispatchers.Unconfined)
    private val locationRepository by lazy {
        val database = createDatabase(driver)
        database.configureMockLocationData(*locations)
        LocationRepository.Impl(database, settings, Dispatchers.Unconfined)
    }

    val viewModel by lazy {
        LocationDetailViewModel(1, locationRepository, Dispatchers.Unconfined)
    }

    @Test
    fun initialState_empty() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(LocationDetailViewModel.State(null), expectViewModelState())
        }
    }

    @Test
    fun initialState_populated() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        settings.putLong(KEY_SELECTED_LOCATION_ID, 1)

        viewModel.stateAndEvents.test {
            assertEquals(LocationDetailViewModel.State(location.toSelectableLocation(true)), expectViewModelState())
        }
    }

    @Test
    fun updateLabel() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)

        viewModel.stateAndEvents.test {
            assertEquals(LocationDetailViewModel.State(location.toSelectableLocation(false)), expectViewModelState())
            expectNoEvents()

            viewModel.performAction(LocationDetailViewModel.Action.SetLabel("Updated"))
            assertEquals(
                LocationDetailViewModel.State(location.copy(label = "Updated").toSelectableLocation(false)),
                expectViewModelState()
            )
        }
    }

    @Test
    fun deleteLocation() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)

        viewModel.stateAndEvents.test {
            assertEquals(LocationDetailViewModel.State(location.toSelectableLocation(false)), expectViewModelState())
            expectNoEvents()

            viewModel.performAction(LocationDetailViewModel.Action.Delete)
            assertEquals(LocationDetailViewModel.State(null), expectViewModelState())
            assertEquals(LocationDetailViewModel.Event.Exit, expectViewModelEvent())
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
