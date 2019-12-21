package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.repository.GeocodeData
import com.russhwolf.soluna.mobile.repository.MockGeocodeRepository
import com.russhwolf.soluna.mobile.repository.MockLocationRepository
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import com.russhwolf.soluna.mobile.suspendTest
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals


class AddLocationViewModelTest : AbstractViewModelTest<AddLocationViewModel, AddLocationViewState>() {
    private val locationRepository = MockLocationRepository()
    private val geocodeRepository =
        MockGeocodeRepository(geocodeMap = mapOf("Home" to GeocodeData(27.18, 62.83, "UTC")))

    override suspend fun createViewModel(): AddLocationViewModel =
        AddLocationViewModel(locationRepository, geocodeRepository, Dispatchers.Unconfined)

    @Test
    fun addLocation_valid() = suspendTest {
        viewModel.addLocation("Home", "27.18", "62.83", "UTC").join()
        val expectedState = AddLocationViewState(exitTrigger = EventTrigger.create())
        assertEquals(expectedState, state)
    }

    @Test
    fun addLocation_invalid() = suspendTest {
        viewModel.addLocation("Home", "Foo", "62.83", "UTC").join()
        val expectedState = AddLocationViewState(exitTrigger = EventTrigger.empty())
        assertEquals(expectedState, state)
    }

    @Test
    fun geocodeLocation_valid() = suspendTest {
        viewModel.geocodeLocation("Home").join()
        val expectedState = AddLocationViewState(geocodeTrigger = EventTrigger.create(GeocodeData(27.18, 62.83, "UTC")))
        assertEquals(expectedState, state)
    }

    @Test
    fun geocodeLocation_invalid() = suspendTest {
        viewModel.geocodeLocation("Away").join()
        val expectedState = AddLocationViewState(geocodeTrigger = EventTrigger.empty())
        assertEquals(expectedState, state)
    }
}
