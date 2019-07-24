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

        val location = repository.getLocation("Test Location")
        assertEquals(
            expected = Location(
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
        val location = repository.getLocation("Test Location")
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

        val dbLocation = database.locationQueries.selectLocationByLabel("Test Location").executeAsOne()

        assertEquals(
            expected = Location(
                id = 1,
                label = "Test Location",
                latitude = 42.3956001,
                longitude = -71.1387674,
                timeZone = "America/New_York"
            ),
            actual = dbLocation
        )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
