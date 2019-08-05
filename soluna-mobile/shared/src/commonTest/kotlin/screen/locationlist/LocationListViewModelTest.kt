package com.russhwolf.soluna.mobile.screen.locationlist

import com.russhwolf.soluna.mobile.LocationDetail
import com.russhwolf.soluna.mobile.LocationSummary
import com.russhwolf.soluna.mobile.MockSolunaRepository
import com.russhwolf.soluna.mobile.runBlockingTest
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class LocationListViewModelTest : AbstractViewModelTest<LocationListViewModel, LocationListViewState>() {
    private var repository = MockSolunaRepository()

    override suspend fun createViewModel(): LocationListViewModel =
        LocationListViewModel(repository, Dispatchers.Unconfined)

    @Test
    fun initialState_empty() = runBlockingTest {
        viewModel.initialLoad.await()
        assertTrue(state.locations.isEmpty())
    }

    @Test
    fun initialState_populated() = runBlockingTest {
        repository = MockSolunaRepository(
            listOf(
                LocationDetail(0, "Home", 27.18, 62.83, "UTC")
            )
        )
        viewModel.initialLoad.await()
        assertEquals(1, state.locations.size)
    }

    @Test
    fun removeLocation() = runBlockingTest {
        repository = MockSolunaRepository(
            listOf(
                LocationDetail(0, "Home", 27.18, 62.83, "UTC")
            )
        )
        viewModel.removeLocation(0).await()
        assertTrue(state.locations.isEmpty())
    }

    @Test
    fun addLocationTrigger() {
        viewModel.navigateToAddLocation()
        assertNotNull(state.addLocationTrigger.consume())
    }

    @Test
    fun locationDetailsTrigger() {
        viewModel.navigateToLocationDetails(LocationSummary(0, "Home"))
        assertEquals(0L, state.locationDetailsTrigger.consume())
    }
}
