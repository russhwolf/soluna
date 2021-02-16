package com.russhwolf.soluna.mobile.screen.locationlist

import com.russhwolf.soluna.mobile.AndroidJUnit4
import com.russhwolf.soluna.mobile.RunWith
import com.russhwolf.soluna.mobile.blockUntilIdle
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.configureMockLocationData
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class LocationListViewModelTest : AbstractViewModelTest<LocationListViewModel, LocationListViewState>() {
    private var locations: Array<Location> = emptyArray()

    private val driver = createInMemorySqlDriver()
    private val locationRepository by lazy {
        val database = createDatabase(driver)
        database.configureMockLocationData(*locations)
        LocationRepository.Impl(database)
    }

    override suspend fun createViewModel(): LocationListViewModel =
        LocationListViewModel(locationRepository, Dispatchers.Unconfined)

    @Test
    fun initialState_empty() = suspendTest {
        awaitLoading()
        assertTrue(state.locations.isEmpty())
    }

    @Test
    fun initialState_populated() = suspendTest {
        locations = arrayOf(Location(1, "Home", 27.18, 62.83, "UTC"))
        awaitLoading()
        assertEquals(listOf(LocationSummary(1, "Home")), state.locations)
    }

    @Test
    fun locationsFlow() = suspendTest {
        awaitLoading()
        locationRepository.addLocation("Home", 27.18, 62.83, "UTC")
        blockUntilIdle()
        delay(50)
        assertEquals(listOf(LocationSummary(1, "Home")), state.locations)
    }

    @Test
    fun removeLocation() = suspendTest {
        locations = arrayOf(Location(1, "Home", 27.18, 62.83, "UTC"))
        viewModel.removeLocation(1).join()
        assertTrue(state.locations.isEmpty())
    }

    @Test
    fun addLocationTrigger() {
        viewModel.navigateToAddLocation()
        assertNotNull(state.addLocationTrigger.consume())
    }

    @Test
    fun locationDetailsTrigger() {
        viewModel.navigateToLocationDetails(LocationSummary(1, "Home"))
        assertEquals(1L, state.locationDetailsTrigger.consume())
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
