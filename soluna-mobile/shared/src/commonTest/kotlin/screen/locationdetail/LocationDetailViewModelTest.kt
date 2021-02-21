package com.russhwolf.soluna.mobile.screen.locationdetail

import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.configureMockLocationData
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import com.russhwolf.soluna.mobile.suspendTest
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.Test

class LocationDetailViewModelTest : AbstractViewModelTest<LocationDetailViewModel, LocationDetailViewState>() {
    private var locations: Array<Location> = emptyArray()
    private val driver = createInMemorySqlDriver()
    private val database = createDatabase(driver)
    private val locationRepository by lazy {
        database.configureMockLocationData(*locations)
        LocationRepository.Impl(database, Dispatchers.Unconfined)
    }

    override suspend fun createViewModel(): LocationDetailViewModel =
        LocationDetailViewModel(1, locationRepository, Dispatchers.Unconfined)

    @Test
    fun initialState_empty() = suspendTest {
        awaitLoading()
        assertState(LocationDetailViewState(location = null))
    }

    @Test
    fun initialState_populated() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        awaitLoading()

        assertState(LocationDetailViewState(location = location))
    }

    @Test
    fun updateLabel() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        awaitLoading()

        viewModel.setLabel("Updated").join()

        assertState(LocationDetailViewState(location = location.copy(label = "Updated")))
    }

    @Test
    fun deleteLocation() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        awaitLoading()

        viewModel.delete().join()

        assertState(
            LocationDetailViewState(
                location = null,
                exitTrigger = EventTrigger.create()
            )
        )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
