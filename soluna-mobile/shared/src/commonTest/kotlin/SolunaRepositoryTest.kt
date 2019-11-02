package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.util.runInBackground
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
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
    fun getLocationsFlow() = runBlocking {
        val values = mutableListOf<List<LocationSummary>>()
        withTimeout(1000) {
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
                .collect {
                    values.add(it)
                }
        }
        assertEquals<List<List<LocationSummary>>>(
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
    fun getLocation_valid() = runBlocking {
        database.insertDummyLocation()

        val location = repository.getLocation(1)
        assertEquals(
            expected = dummyLocation,
            actual = location
        )
    }

    @Test
    fun getLocation_invalid() = runBlocking {
        val location = repository.getLocation(1)
        assertNull(location)
    }

    @Test
    fun addLocation_valid() = runBlocking {
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
    fun deleteLocation_valid() = runBlocking {
        database.insertDummyLocation()

        repository.deleteLocation(1)

        val locations = database.locationQueries.selectAllLocations().executeAsList()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun updateLocationLabel_valid() = runBlocking {
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

    @Test
    fun getReminders_empty() = runBlocking {
        val reminders = repository.getReminders()
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun getReminders_populated() = runBlocking {
        database.insertDummyLocation()
        database.insertDummyReminder()

        val reminders = repository.getReminders()

        assertEquals(1, reminders.size)
        assertEquals(
            expected = dummyReminder,
            actual = reminders[0]
        )
    }

    @Test
    fun getRemindersForLocation_empty() = runBlocking {
        database.insertDummyLocation()
        val reminders = repository.getReminders(1)
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun getRemindersForLocation_populated() = runBlocking {
        database.insertDummyLocation()
        database.insertDummyReminder()

        val reminders = repository.getReminders(1)

        assertEquals(1, reminders.size)
        assertEquals(
            expected = dummyReminder,
            actual = reminders[0]
        )
    }

    @Test
    fun getRemindersForLocation_invalid() = runBlocking {
        database.insertDummyLocation()
        database.insertDummyReminder()

        val reminders = repository.getReminders(2)

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun getRemindersForOtherLocation_empty() = runBlocking {
        database.insertDummyLocation(1)
        database.insertDummyLocation(2)
        database.insertDummyReminder(1)

        val reminders = repository.getReminders(2)

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun addReminder_valid() = runBlocking {
        database.insertDummyLocation()

        repository.addReminder(
            locationId = 1,
            type = ReminderType.Sunset,
            minutesBefore = 15,
            enabled = true
        )

        val reminder = database.reminderQueries.selectAllReminders().executeAsOne()
        assertEquals(
            expected = dummyReminder,
            actual = reminder
        )
    }

    @Test
    fun deleteReminder_valid() = runBlocking {
        database.insertDummyLocation()
        database.insertDummyReminder()

        repository.deleteReminder(1)

        val reminders = database.reminderQueries.selectAllReminders().executeAsList()
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun updateReminder_valid() = runBlocking {
        database.insertDummyLocation()
        database.insertDummyReminder()

        repository.updateReminder(
            id = 1,
            minutesBefore = 30,
            enabled = false
        )

        val reminder = database.reminderQueries.selectAllReminders().executeAsOne()
        assertEquals(
            expected = ReminderWithLocation.Impl(
                id = 1,
                locationId = 1,
                locationLabel = "Test Location 1",
                type = ReminderType.Sunset,
                minutesBefore = 30,
                enabled = false
            ),
            actual = reminder
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

private val dummyLocation = Location.Impl(
    id = 1,
    label = "Test Location 1",
    latitude = 42.3956001,
    longitude = -71.1387674,
    timeZone = "America/New_York"
)

private val dummyReminder = ReminderWithLocation.Impl(
    id = 1,
    locationId = 1,
    locationLabel = "Test Location 1",
    type = ReminderType.Sunset,
    minutesBefore = 15,
    enabled = true
)

private fun SolunaDb.insertDummyLocation(id: Long = 1) {
    locationQueries.insertLocation(
        label = "Test Location $id",
        latitude = 42.3956001,
        longitude = -71.1387674,
        timeZone = "America/New_York"
    )
}

private fun SolunaDb.insertDummyReminder(locationId: Long = 1) {
    reminderQueries.insertReminder(
        locationId = locationId,
        type = ReminderType.Sunset,
        minutesBefore = 15,
        enabled = true
    )
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

