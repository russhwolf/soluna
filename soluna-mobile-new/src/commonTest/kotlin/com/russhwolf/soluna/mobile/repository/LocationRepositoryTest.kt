package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.db.invoke
import com.russhwolf.soluna.mobile.db.sqldelight.LocationSummary
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb
import com.russhwolf.soluna.mobile.repository.LocationRepository.Companion.KEY_SELECTED_LOCATION_ID
import com.russhwolf.soluna.mobile.test.createInMemorySqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LocationRepositoryTest {

    private val driver = createInMemorySqlDriver()
    private val database = SolunaDb(driver)
    private val settings = MapSettings().toFlowSettings(Dispatchers.Unconfined)
    private val repository = LocationRepository(database, settings, Dispatchers.Unconfined)

    @Test
    fun getLocations() = runTest {
        repository.getLocations().test {
            assertEquals(emptyList(), awaitItem())

            database.insertDummyLocation(1)
            assertEquals(
                expected = listOf(
                    SelectableLocationSummary(
                        id = 1,
                        label = "Test Location 1",
                        selected = false
                    )
                ),
                actual = awaitItem()
            )
            expectNoEvents()

            database.insertDummyLocation(2)
            assertEquals(
                expected = listOf(
                    SelectableLocationSummary(
                        id = 1,
                        label = "Test Location 1",
                        selected = false
                    ),
                    SelectableLocationSummary(
                        id = 2,
                        label = "Test Location 2",
                        selected = false
                    )
                ),
                actual = awaitItem()
            )
            expectNoEvents()

            settings.putLong(KEY_SELECTED_LOCATION_ID, 2)
            assertEquals(
                expected = listOf(
                    SelectableLocationSummary(
                        id = 1,
                        label = "Test Location 1",
                        selected = false
                    ),
                    SelectableLocationSummary(
                        id = 2,
                        label = "Test Location 2",
                        selected = true
                    )
                ),
                actual = awaitItem()
            )
            expectNoEvents()
        }
    }

    @Test
    fun getLocation() = runTest {
        database.insertDummyLocation()

        repository.getLocation(1).test {
            assertEquals(dummyLocation, awaitItem())
            expectNoEvents()

            database.locationQueries.updateLocationLabelById("Updated location", 1)
            assertEquals(dummyLocation.copy(label = "Updated location"), awaitItem())
            expectNoEvents()

            database.insertDummyLocation(2)
            expectNoEvents()

            settings.putLong(KEY_SELECTED_LOCATION_ID, 2)
            expectNoEvents()

            settings.putLong(KEY_SELECTED_LOCATION_ID, 1)
            assertEquals(dummyLocation.copy(label = "Updated location", selected = true), awaitItem())
            expectNoEvents()

            database.locationQueries.deleteLocationById(1)
            assertNull(awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun addLocation_valid() = runTest {
        repository.addLocation(
            label = "Test Location 1",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        val dbLocation = database.locationQueries.selectLocationById(1).executeAsOne()

        assertEquals(
            expected = dummyLocation,
            actual = dbLocation.toSelectableLocation(false)
        )
    }

    @Test
    fun deleteLocation_valid() = runTest {
        database.insertDummyLocation()

        repository.deleteLocation(1)

        val locations = database.locationQueries.selectAllLocations().executeAsList()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun updateLocationLabel_valid() = runTest {
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
    fun getSelectedItem() = runTest {
        repository.getSelectedLocation().test {
            assertNull(awaitItem())
            expectNoEvents()

            database.insertDummyLocation()
            expectNoEvents()

            settings.putLong(KEY_SELECTED_LOCATION_ID, dummyLocation.id)
            assertEquals(dummyLocation.copy(selected = true), awaitItem())
            expectNoEvents()

            database.locationQueries.deleteLocationById(dummyLocation.id)
            assertNull(awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun toggleSelectedLocation() = runTest {
        database.insertDummyLocation(1)
        database.insertDummyLocation(2)

        repository.getSelectedLocation().test {
            assertNull(awaitItem())


            repository.toggleSelectedLocation(1)
            assertEquals(dummyLocation.copy(id = 1, label = "Test Location 1", selected = true), awaitItem())

            repository.toggleSelectedLocation(2)
            assertEquals(dummyLocation.copy(id = 2, label = "Test Location 2", selected = true), awaitItem())

            repository.toggleSelectedLocation(2)
            assertNull(awaitItem())
        }
    }


    @AfterTest
    fun tearDown() {
        driver.close()
    }
}

private val dummyLocation = SelectableLocation(
    id = 1,
    label = "Test Location 1",
    latitude = 42.3956001,
    longitude = -71.1387674,
    timeZone = "America/New_York",
    selected = false
)

private fun SolunaDb.insertDummyLocation(id: Long = 1) {
    locationQueries.insertLocation(
        label = "Test Location $id",
        latitude = 42.3956001,
        longitude = -71.1387674,
        timeZone = "America/New_York"
    )
}

