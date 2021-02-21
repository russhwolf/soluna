package com.russhwolf.soluna.mobile.db

import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SolunaDbTest {

    private lateinit var driver: SqlDriver
    private lateinit var database: SolunaDb

    @BeforeTest
    fun setup() {
        driver = createInMemorySqlDriver()
        database = createDatabase(driver)
    }

    @Test
    fun locationQueries_happyPath() {
        val initialLocations = database.locationQueries.selectAllLocations().executeAsList()
        assertTrue(initialLocations.isEmpty())

        database.locationQueries.insertLocation(
            label = "Test Location",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        val updatedLocations = database.locationQueries.selectAllLocations().executeAsList()
        assertEquals(1, updatedLocations.size)
        assertEquals(
            expected = LocationSummary(
                id = 1,
                label = "Test Location"
            ),
            actual = updatedLocations[0]
        )

        val location = database.locationQueries.selectLocationById(1).executeAsOne()
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

        database.locationQueries.updateLocationLabelById("Updated Location", location.id)

        val updatedLocation = database.locationQueries.selectLocationById(1).executeAsOne()
        assertEquals(
            expected = Location(
                id = 1,
                label = "Updated Location",
                latitude = 42.3956001,
                longitude = -71.1387674,
                timeZone = "America/New_York"
            ),
            actual = updatedLocation
        )

        database.locationQueries.deleteLocationById(1)
        val finalLocations = database.locationQueries.selectAllLocations().executeAsList()
        assertTrue(finalLocations.isEmpty())
    }

    @Test
    fun reminderQueries_happyPath() {
        database.locationQueries.insertLocation(
            label = "Location 1",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )
        database.locationQueries.insertLocation(
            label = "Location 2",
            latitude = 27.7790026,
            longitude = -82.7949071,
            timeZone = "America/New_York"
        )

        val initialReminders = database.reminderQueries.selectAllReminders().executeAsList()
        assertTrue(initialReminders.isEmpty())

        database.reminderQueries.insertReminder(
            locationId = 1,
            type = ReminderType.Sunset,
            minutesBefore = 15,
            enabled = true
        )
        database.reminderQueries.insertReminder(
            locationId = 2,
            type = ReminderType.Sunrise,
            minutesBefore = 15,
            enabled = false
        )
        database.reminderQueries.insertReminder(
            locationId = 2,
            type = ReminderType.Sunset,
            minutesBefore = 30,
            enabled = true
        )

        val insertedReminders = database.reminderQueries.selectAllReminders().executeAsList()
        assertEquals(
            expected = listOf(
                ReminderWithLocation(
                    id = 1,
                    locationId = 1,
                    locationLabel = "Location 1",
                    locationLatitude = 42.3956001,
                    locationLongitude = -71.1387674,
                    locationTimeZone = "America/New_York",
                    type = ReminderType.Sunset,
                    minutesBefore = 15,
                    enabled = true
                ),
                ReminderWithLocation(
                    id = 2,
                    locationId = 2,
                    locationLabel = "Location 2",
                    locationLatitude = 27.7790026,
                    locationLongitude = -82.7949071,
                    locationTimeZone = "America/New_York",
                    type = ReminderType.Sunrise,
                    minutesBefore = 15,
                    enabled = false
                ),
                ReminderWithLocation(
                    id = 3,
                    locationId = 2,
                    locationLabel = "Location 2",
                    locationLatitude = 27.7790026,
                    locationLongitude = -82.7949071,
                    locationTimeZone = "America/New_York",
                    type = ReminderType.Sunset,
                    minutesBefore = 30,
                    enabled = true
                )
            ),
            actual = insertedReminders
        )

        val insertedRemindersForLocation1 =
            database.reminderQueries.selectRemindersByLocationId(1).executeAsList()
        assertEquals(
            expected = listOf(
                Reminder(
                    id = 1,
                    locationId = 1,
                    type = ReminderType.Sunset,
                    minutesBefore = 15,
                    enabled = true
                )
            ),
            actual = insertedRemindersForLocation1
        )

        database.reminderQueries.updateReminderEnabledById(false, 1)
        database.reminderQueries.updateReminderMinutesBeforeById(45, 3)

        val updatedReminders = database.reminderQueries.selectAllReminders().executeAsList()
        assertEquals(
            expected = listOf(
                ReminderWithLocation(
                    id = 1,
                    locationId = 1,
                    locationLabel = "Location 1",
                    locationLatitude = 42.3956001,
                    locationLongitude = -71.1387674,
                    locationTimeZone = "America/New_York",
                    type = ReminderType.Sunset,
                    minutesBefore = 15,
                    enabled = false
                ),
                ReminderWithLocation(
                    id = 2,
                    locationId = 2,
                    locationLabel = "Location 2",
                    locationLatitude = 27.7790026,
                    locationLongitude = -82.7949071,
                    locationTimeZone = "America/New_York",
                    type = ReminderType.Sunrise,
                    minutesBefore = 15,
                    enabled = false
                ),
                ReminderWithLocation(
                    id = 3,
                    locationId = 2,
                    locationLabel = "Location 2",
                    locationLatitude = 27.7790026,
                    locationLongitude = -82.7949071,
                    locationTimeZone = "America/New_York",
                    type = ReminderType.Sunset,
                    minutesBefore = 45,
                    enabled = true
                )
            ),
            actual = updatedReminders
        )

        database.reminderQueries.deleteReminderById(2)

        val updatedRemindersAfterDelete = database.reminderQueries.selectAllReminders().executeAsList()
        assertEquals(
            expected = listOf(
                ReminderWithLocation(
                    id = 1,
                    locationId = 1,
                    locationLabel = "Location 1",
                    locationLatitude = 42.3956001,
                    locationLongitude = -71.1387674,
                    locationTimeZone = "America/New_York",
                    type = ReminderType.Sunset,
                    minutesBefore = 15,
                    enabled = false
                ),
                ReminderWithLocation(
                    id = 3,
                    locationId = 2,
                    locationLabel = "Location 2",
                    locationLatitude = 27.7790026,
                    locationLongitude = -82.7949071,
                    locationTimeZone = "America/New_York",
                    type = ReminderType.Sunset,
                    minutesBefore = 45,
                    enabled = true
                )
            ),
            actual = updatedRemindersAfterDelete
        )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
