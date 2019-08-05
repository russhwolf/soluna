package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.squareup.sqldelight.db.SqlDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SolunaRepositoryTest {
    private lateinit var driver: SqlDriver
    private lateinit var database: SolunaDb
    private lateinit var repository: SolunaRepository

    @BeforeTest
    fun setup() {
        driver = createInMemorySqlDriver()
        database = createDatabase(driver)
        repository = SolunaRepository.Impl(database)
    }

    @Test
    fun getLocations_empty() = runBlocking {
        val locations = repository.getLocations()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun getLocations_populated() = runBlocking {
        database.locationQueries.insertLocation(
            label = "Test Location",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        val locations = repository.getLocations()

        assertEquals(1, locations.size)
        assertEquals(
            expected = LocationSummary(
                id = 1,
                label = "Test Location"
            ),
            actual = locations[0]
        )
    }

    @Test
    fun getLocation_valid() = runBlocking {
        database.locationQueries.insertLocation(
            label = "Test Location",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        val location = repository.getLocation(1)
        assertEquals(
            expected = LocationDetail(
                id = 1,
                label = "Test Location",
                latitude = 42.3956001,
                longitude = -71.1387674,
                timeZone = "America/New_York"
            ),
            actual = location
        )
    }

    @Test
    fun getLocation_invalid() = runBlocking {
        val location = repository.getLocation(1)
        assertNull(location)
    }

    @Test
    fun insertLocation_valid() = runBlocking {
        repository.addLocation(
            label = "Test Location",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        val dbLocation = database.locationQueries.selectLocationById(1).executeAsOne()

        assertEquals(
            expected = LocationDetail(
                id = 1,
                label = "Test Location",
                latitude = 42.3956001,
                longitude = -71.1387674,
                timeZone = "America/New_York"
            ),
            actual = dbLocation
        )
    }

    @Test
    fun deleteLocation_valid() = runBlocking {
        repository.addLocation(
            label = "Test Location",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        database.locationQueries.deleteLocationById(1)

        val locations = database.locationQueries.selectAllLocations().executeAsList()
        assertTrue(locations.isEmpty())
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
