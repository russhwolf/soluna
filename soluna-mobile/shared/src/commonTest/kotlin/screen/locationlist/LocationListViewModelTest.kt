package com.russhwolf.soluna.mobile.screen.locationlist

import com.russhwolf.soluna.mobile.MockLocationRepository
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.suspendTest
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class LocationListViewModelTest : AbstractViewModelTest<LocationListViewModel, LocationListViewState>() {
    private var locationRepository = MockLocationRepository()

    override suspend fun createViewModel(): LocationListViewModel =
        LocationListViewModel(locationRepository, Dispatchers.Unconfined)

    @Test
    fun initialState_empty() = suspendTest {
        awaitLoading()
        assertTrue(state.locations.isEmpty())
    }

    @Test
    fun initialState_populated() = suspendTest {
        locationRepository = MockLocationRepository(
            listOf(
                Location.Impl(0, "Home", 27.18, 62.83, "UTC")
            )
        )
        awaitLoading()
        assertEquals(listOf(LocationSummary.Impl(0, "Home")), state.locations)
    }

    @Test
    fun locationsFlow() = suspendTest {
        awaitLoading()
        locationRepository.addLocation("Home", 27.18, 62.83, "UTC")
        assertEquals(listOf(LocationSummary.Impl(0, "Home")), state.locations)
    }

    @Test
    fun removeLocation() = suspendTest {
        locationRepository = MockLocationRepository(
            listOf(
                Location.Impl(0, "Home", 27.18, 62.83, "UTC")
            )
        )
        viewModel.removeLocation(0).join()
        assertTrue(state.locations.isEmpty())
    }

    @Test
    fun addLocationTrigger() {
        viewModel.navigateToAddLocation()
        assertNotNull(state.addLocationTrigger.consume())
    }

    @Test
    fun locationDetailsTrigger() {
        viewModel.navigateToLocationDetails(LocationSummary.Impl(0, "Home"))
        assertEquals(0L, state.locationDetailsTrigger.consume())
    }
}
