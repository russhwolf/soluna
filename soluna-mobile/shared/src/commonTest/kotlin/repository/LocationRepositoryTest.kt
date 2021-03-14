package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.settings.MockSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LocationRepositoryTest {

    private val driver = createInMemorySqlDriver()
    private val database = createDatabase(driver)
    private val settings = MockSettings().toFlowSettings(Dispatchers.Unconfined)
    private val repository = LocationRepository.Impl(database, settings, Dispatchers.Unconfined)

    @Test
    fun getLocations() = suspendTest {
        repository.getLocations().test {
            assertEquals(emptyList(), expectItem())

            database.insertDummyLocation(1)
            assertEquals(
                expected = listOf(
                    LocationSummary(
                        id = 1,
                        label = "Test Location 1"
                    )
                ),
                actual = expectItem()
            )
            expectNoEvents()

            database.insertDummyLocation(2)
            assertEquals(
                expected = listOf(
                    LocationSummary(
                        id = 1,
                        label = "Test Location 1"
                    ),
                    LocationSummary(
                        id = 2,
                        label = "Test Location 2"
                    )
                ),
                actual = expectItem()
            )
            expectNoEvents()
        }
    }

    @Test
    fun getLocation() = suspendTest {
        database.insertDummyLocation()

        repository.getLocation(1).test {
            assertEquals(dummyLocation, expectItem())
            expectNoEvents()

            database.locationQueries.updateLocationLabelById("Updated location", 1)
            assertEquals(dummyLocation.copy(label = "Updated location"), expectItem())
            expectNoEvents()

            database.insertDummyLocation(2)
            expectNoEvents()

            database.locationQueries.deleteLocationById(1)
            assertNull(expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun addLocation_valid() = suspendTest {
        repository.addLocation(
            label = "Test Location 1",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        val dbLocation = database.locationQueries.selectLocationById(1).executeAsOne()

        assertEquals(
            expected = dummyLocation,
            actual = dbLocation
        )
    }

    @Test
    fun deleteLocation_valid() = suspendTest {
        database.insertDummyLocation()

        repository.deleteLocation(1)

        val locations = database.locationQueries.selectAllLocations().executeAsList()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun updateLocationLabel_valid() = suspendTest {
        database.insertDummyLocation()

        repository.updateLocationLabel(1, "Updated Location")

        val locations = database.locationQueries.selectAllLocations().executeAsList()
        assertEquals(
            expected = listOf(
                LocationSummary(
                    id = 1,
                    label = "Updated Location"
                )
            ),
            actual = locations
        )
    }

    @Test
    fun getSelectedItem() = suspendTest {
        repository.getSelectedLocation().test {
            assertNull(expectItem())
            expectNoEvents()

            database.insertDummyLocation()
            expectNoEvents()

            settings.putLong("selected_location_id", dummyLocation.id)
            assertEquals(dummyLocation, expectItem())
            expectNoEvents()

            database.locationQueries.deleteLocationById(dummyLocation.id)
            assertNull(expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun setSelectedItem() = suspendTest {
        database.insertDummyLocation()

        repository.setSelectedLocation(dummyLocation)

        assertEquals(dummyLocation, repository.getSelectedLocation().first())
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}

private val dummyLocation = Location(
    id = 1,
    label = "Test Location 1",
    latitude = 42.3956001,
    longitude = -71.1387674,
    timeZone = "America/New_York"
)

// TODO this is causing a weird compile error if there's a default value
private fun SolunaDb.insertDummyLocation() = insertDummyLocation(1)
private fun SolunaDb.insertDummyLocation(id: Long) {
    locationQueries.insertLocation(
        label = "Test Location $id",
        latitude = 42.3956001,
        longitude = -71.1387674,
        timeZone = "America/New_York"
    )
}

