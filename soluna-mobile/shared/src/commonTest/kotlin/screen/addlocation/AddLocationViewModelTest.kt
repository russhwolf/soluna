package com.russhwolf.soluna.mobile.screen.addlocation

import app.cash.turbine.test
import com.russhwolf.settings.MockSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.DeviceLocationResult
import com.russhwolf.soluna.mobile.repository.FakeCurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.FakeDeviceLocationService
import com.russhwolf.soluna.mobile.repository.GeocodeData
import com.russhwolf.soluna.mobile.repository.GeocodeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.SelectableLocation
import com.russhwolf.soluna.mobile.repository.createGeocodeMockClientEngine
import com.russhwolf.soluna.mobile.screen.expectViewModelEvent
import com.russhwolf.soluna.mobile.screen.expectViewModelState
import com.russhwolf.soluna.mobile.screen.stateAndEvents
import com.russhwolf.soluna.mobile.suspendTest
import io.ktor.client.features.logging.EMPTY
import io.ktor.client.features.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddLocationViewModelTest {
    private val driver = createInMemorySqlDriver()
    private val settings = MockSettings().toFlowSettings(Dispatchers.Unconfined)
    private val locationRepository =
        LocationRepository.Impl(
            createDatabase(driver),
            settings,
            Dispatchers.Unconfined
        )
    private val geocodeRepository = GeocodeRepository.Impl(
        GoogleApiClient.Impl(
            createGeocodeMockClientEngine(
                geocodeMap = mapOf("Home" to GeocodeData(27.18, 62.83, "UTC"))
            ),
            Logger.EMPTY
        ),
        FakeCurrentTimeRepository()
    )

    private val viewModel = AddLocationViewModel(
        locationRepository,
        geocodeRepository,
        FakeDeviceLocationService(true, DeviceLocationResult.RequestFailed),
        Dispatchers.Unconfined
    )
        .also { it.activate() }

    @Test
    fun addLocation_valid() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(AddLocationViewModel.State(), expectViewModelState())

            viewModel.performAction(AddLocationViewModel.Action.CreateLocation("Home", "27.18", "62.83", "UTC"))
            assertEquals(AddLocationViewModel.Event.Exit, expectViewModelEvent())
            assertEquals(
                SelectableLocation(1, "Home", 27.18, 62.83, "UTC", false),
                locationRepository.getLocation(1).first()
            )
        }
    }

    @Test
    fun addLocation_invalidLatitude() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(AddLocationViewModel.State(), expectViewModelState())

            viewModel.performAction(AddLocationViewModel.Action.CreateLocation("Home", "Foo", "62.83", "UTC"))
            assertEquals(AddLocationViewModel.State(latitudeFormatError = true), expectViewModelState())

            viewModel.performAction(AddLocationViewModel.Action.CreateLocation("Home", "27.18", "62.83", "UTC"))
            assertEquals(AddLocationViewModel.State(), expectViewModelState())
            assertEquals(AddLocationViewModel.Event.Exit, expectViewModelEvent())
        }
    }

    @Test
    fun addLocation_invalidLongitude() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(AddLocationViewModel.State(), expectViewModelState())

            viewModel.performAction(AddLocationViewModel.Action.CreateLocation("Home", "27.18", "Bar", "UTC"))
            assertEquals(AddLocationViewModel.State(longitudeFormatError = true), expectViewModelState())

            viewModel.performAction(AddLocationViewModel.Action.CreateLocation("Home", "27.18", "62.83", "UTC"))
            assertEquals(AddLocationViewModel.State(), expectViewModelState())
            assertEquals(AddLocationViewModel.Event.Exit, expectViewModelEvent())
        }
    }

    @Test
    fun addLocation_invalidLatitudeAndLongitude() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(AddLocationViewModel.State(), expectViewModelState())

            viewModel.performAction(AddLocationViewModel.Action.CreateLocation("Home", "Foo", "Bar", "UTC"))
            assertEquals(
                AddLocationViewModel.State(latitudeFormatError = true, longitudeFormatError = true),
                expectViewModelState()
            )

            viewModel.performAction(AddLocationViewModel.Action.CreateLocation("Home", "27.18", "62.83", "UTC"))
            assertEquals(AddLocationViewModel.State(), expectViewModelState())
            assertEquals(AddLocationViewModel.Event.Exit, expectViewModelEvent())
        }
    }

    @Test
    fun geocodeLocation_valid() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(AddLocationViewModel.State(), expectViewModelState())

            viewModel.performAction(AddLocationViewModel.Action.GeocodeLocation("Home"))
            assertEquals(AddLocationViewModel.Event.ShowGeocodeData(27.18, 62.83, "UTC"), expectViewModelEvent())
        }
    }

    @Test
    fun geocodeLocation_invalid() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(AddLocationViewModel.State(), expectViewModelState())

            viewModel.performAction(AddLocationViewModel.Action.GeocodeLocation("Away"))
            assertEquals(AddLocationViewModel.State(geocodeError = true), expectViewModelState())

            viewModel.performAction(AddLocationViewModel.Action.GeocodeLocation("Home"))
            assertEquals(AddLocationViewModel.State(), expectViewModelState())
            assertEquals(AddLocationViewModel.Event.ShowGeocodeData(27.18, 62.83, "UTC"), expectViewModelEvent())
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
