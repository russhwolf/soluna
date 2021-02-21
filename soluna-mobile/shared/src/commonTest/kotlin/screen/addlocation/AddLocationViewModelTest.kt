package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.GeocodeData
import com.russhwolf.soluna.mobile.repository.GeocodeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.createGeocodeMockClientEngine
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import com.russhwolf.soluna.mobile.suspendTest
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.Test

class AddLocationViewModelTest : AbstractViewModelTest<AddLocationViewModel, AddLocationViewState>() {
    private val driver = createInMemorySqlDriver()
    private val locationRepository = LocationRepository.Impl(createDatabase(driver), Dispatchers.Unconfined)
    private val geocodeRepository = GeocodeRepository.Impl(
        GoogleApiClient.Impl(
            createGeocodeMockClientEngine(
                geocodeMap = mapOf("Home" to GeocodeData(27.18, 62.83, "UTC"))
            )
        )
    )

    override suspend fun createViewModel(): AddLocationViewModel =
        AddLocationViewModel(locationRepository, geocodeRepository, Dispatchers.Unconfined)

    @Test
    fun addLocation_valid() = suspendTest {
        viewModel.addLocation("Home", "27.18", "62.83", "UTC").join()
        val expectedState = AddLocationViewState(exitTrigger = EventTrigger.create())
        assertState(expectedState)
    }

    @Test
    fun addLocation_invalid() = suspendTest {
        viewModel.addLocation("Home", "Foo", "62.83", "UTC").join()
        val expectedState = AddLocationViewState(exitTrigger = EventTrigger.empty())
        assertState(expectedState, error = NumberFormatException())
    }

    @Test
    fun geocodeLocation_valid() = suspendTest {
        viewModel.geocodeLocation("Home").join()
        val expectedState = AddLocationViewState(geocodeTrigger = EventTrigger.create(GeocodeData(27.18, 62.83, "UTC")))
        assertState(expectedState)
    }

    @Test
    fun geocodeLocation_invalid() = suspendTest {
        viewModel.geocodeLocation("Away").join()
        val expectedState = AddLocationViewState(geocodeTrigger = EventTrigger.empty())
        assertState(expectedState)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
