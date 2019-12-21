package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.AndroidJUnit4
import com.russhwolf.soluna.mobile.RunWith
import com.russhwolf.soluna.mobile.blockUntilIdle
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.suspendTest
import com.russhwolf.soluna.mobile.util.runInBackground
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class LocationRepositoryTest {

    private lateinit var driver: SqlDriver
    private lateinit var database: SolunaDb
    private lateinit var repository: LocationRepository

    @BeforeTest
    fun setup() {
        driver = createInMemorySqlDriver()
        database = createDatabase(driver)
        repository = LocationRepository.Impl(database)
    }

    @Test
    fun getLocations_empty() = suspendTest {
        val locations = repository.getLocations()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun getLocations_populated() = suspendTest {
        database.insertDummyLocation()

        val locations = repository.getLocations()

        assertEquals(1, locations.size)
        assertEquals(
            expected = LocationSummary.Impl(
                id = 1,
                label = "Test Location 1"
            ),
            actual = locations[0]
        )
    }

    @Test
    fun getLocationsFlow() = suspendTest {
        val values = withTimeout(1000) {
            repository.getLocationsFlow()
                .onStart {
                    launch {
                        delay(5)
                        runInBackground { database.insertDummyLocation(1) }
                        blockUntilIdle()
                        delay(5)
                        runInBackground { database.insertDummyLocation(2) }
                        blockUntilIdle()
                    }
                }
                .take(2)
                .toList()
        }
        assertEquals(
            expected = listOf(
                listOf(
                    LocationSummary.Impl(
                        id = 1,
                        label = "Test Location 1"
                    )
                ),
                listOf(
                    LocationSummary.Impl(
                        id = 1,
                        label = "Test Location 1"
                    ),
                    LocationSummary.Impl(
                        id = 2,
                        label = "Test Location 2"
                    )
                )
            ),
            actual = values
        )
    }

    @Test
    fun getLocation_valid() = suspendTest {
        database.insertDummyLocation()

        val location = repository.getLocation(1)
        assertEquals(
            expected = dummyLocation,
            actual = location
        )
    }

    @Test
    fun getLocation_invalid() = suspendTest {
        val location = repository.getLocation(1)
        assertNull(location)
    }

    @Test
    fun getLocationFlow() = suspendTest {
        database.insertDummyLocation()

        val values = withTimeout(1000) {
            repository.getLocationFlow(1)
                .onStart {
                    launch {
                        delay(5)
                        runInBackground { database.locationQueries.updateLocationLabelById("Updated location", 1) }
                        blockUntilIdle()
                        delay(5)
                        runInBackground { database.insertDummyLocation(2) }
                        blockUntilIdle()
                        delay(5)
                        runInBackground { database.locationQueries.deleteLocationById(1) }
                        blockUntilIdle()
                    }
                }
                .take(2)
                .toList()
        }
        assertEquals(
            expected = listOf(
                dummyLocation.copy(label = "Updated location"),
                null
            ),
            actual = values
        )
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
                LocationSummary.Impl(
                    id = 1,
                    label = "Updated Location"
                )
            ),
            actual = locations
        )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}

private val dummyLocation = Location.Impl(
    id = 1,
    label = "Test Location 1",
    latitude = 42.3956001,
    longitude = -71.1387674,
    timeZone = "America/New_York"
)

private fun SolunaDb.insertDummyLocation(id: Long = 1) {
    locationQueries.insertLocation(
        label = "Test Location $id",
        latitude = 42.3956001,
        longitude = -71.1387674,
        timeZone = "America/New_York"
    )
}
