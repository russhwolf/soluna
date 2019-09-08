package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.SelectAllLocations
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class SolunaRepositoryTest {
    private val googleApiClient = GoogleApiClient.Impl(
        createMockEngine(
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )
    )

    private lateinit var driver: SqlDriver
    private lateinit var database: SolunaDb
    private lateinit var repository: SolunaRepository

    @BeforeTest
    fun setup() {
        driver = createInMemorySqlDriver()
        database = createDatabase(driver)
        repository = SolunaRepository.Impl(database, googleApiClient)
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
            expected = Location.Impl(
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
        database.locationQueries.insertLocation(
            label = "Test Location",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        repository.deleteLocation(1)

        val locations = database.locationQueries.selectAllLocations().executeAsList()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun updateLocationLabel_valid() = runBlocking {
        database.locationQueries.insertLocation(
            label = "Test Location",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        repository.updateLocationLabel(1, "Updated Location")

        val locations = database.locationQueries.selectAllLocations().executeAsList()
        assertEquals(
            expected = listOf(
                SelectAllLocations.Impl(
                    id = 1,
                    label = "Updated Location"
                )
            ),
            actual = locations
        )
    }

    @Test
    fun geocodeLocation_valid() = runBlocking {
        val geocodeData = repository.geocodeLocation("Test Location")

        assertEquals(
            expected = GeocodeData(
                latitude = 42.3956001,
                longitude = -71.1387674,
                timeZone = "America/New_York"
            ),
            actual = geocodeData
        )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}

private fun createMockEngine(latitude: Double, longitude: Double, timeZone: String) = MockEngine { httpRequestData ->
    val body = when (val path = httpRequestData.url.encodedPath) {
        "place/autocomplete/json" ->
            """{ "predictions" : [{ "place_id" : "ChIJZeH1eyl344kRA3v52Jl3kHo" }], "status" : "OK" }"""
        "geocode/json" ->
            """{ "results" : [{ "geometry" : { "location" : { "lat" : $latitude, "lng" : $longitude }}} ], "status" : "OK" }"""
        "timezone/json" ->
            """{ "dstOffset" : 3600, "rawOffset" : -18000, "status" : "OK", "timeZoneId" : "$timeZone", "timeZoneName" : "Eastern Daylight Time" }"""
        else -> fail("Invalid path $path")
    }
    respond(
        content = body,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}
