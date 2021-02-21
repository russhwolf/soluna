package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.suspendTest
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReminderRepositoryTest {

    private lateinit var driver: SqlDriver
    private lateinit var database: SolunaDb
    private lateinit var repository: ReminderRepository

    @BeforeTest
    fun setup() {
        driver = createInMemorySqlDriver()
        database = createDatabase(driver)
        repository = ReminderRepository.Impl(database, Dispatchers.Unconfined)
    }

    @Test
    fun getReminders_empty() = suspendTest {
        val reminders = repository.getReminders()
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun getReminders_populated() = suspendTest {
        database.insertDummyLocation()
        database.insertDummyReminder()

        val reminders = repository.getReminders()

        assertEquals(1, reminders.size)
        assertEquals(
            expected = dummyReminderWithLocation,
            actual = reminders[0]
        )
    }

    @Test
    fun getRemindersFlow() = suspendTest {
        database.insertDummyLocation()
        database.insertDummyReminder()

        repository.getRemindersFlow().test {
            assertEquals(listOf(dummyReminderWithLocation), expectItem())
            expectNoEvents()

            database.reminderQueries.deleteReminderById(1)
            assertEquals(emptyList(), expectItem())
            expectNoEvents()

            database.insertDummyReminder()
            assertEquals(listOf(dummyReminderWithLocation.copy(id = 2)), expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun getRemindersForLocation_empty() = suspendTest {
        database.insertDummyLocation()
        val reminders = repository.getRemindersForLocation(1)
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun getRemindersForLocation_populated() = suspendTest {
        database.insertDummyLocation()
        database.insertDummyReminder()

        val reminders = repository.getRemindersForLocation(1)

        assertEquals(1, reminders.size)
        assertEquals(
            expected = dummyReminder,
            actual = reminders[0]
        )
    }

    @Test
    fun getRemindersForLocation_invalid() = suspendTest {
        database.insertDummyLocation()
        database.insertDummyReminder()

        val reminders = repository.getRemindersForLocation(2)

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun getRemindersForLocationFlow() = suspendTest {
        database.insertDummyLocation(1)
        database.insertDummyLocation(2)
        database.insertDummyReminder(1)

        repository.getRemindersForLocationFlow(1).test {
            assertEquals(listOf(dummyReminder), expectItem())
            expectNoEvents()

            database.reminderQueries.deleteReminderById(1)
            assertEquals(emptyList(), expectItem())
            expectNoEvents()

            database.insertDummyReminder(2)
            // TODO is it a SqlDelight bug that this re-emits?
            assertEquals(emptyList(), expectItem())
            expectNoEvents()

            database.insertDummyReminder(1)
            assertEquals(listOf(dummyReminder.copy(id = 3)), expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun getRemindersForOtherLocation_empty() = suspendTest {
        database.insertDummyLocation(1)
        database.insertDummyLocation(2)
        database.insertDummyReminder(1)

        val reminders = repository.getRemindersForLocation(2)

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun addReminder_valid() = suspendTest {
        database.insertDummyLocation()

        repository.addReminder(
            locationId = 1,
            type = ReminderType.Sunset,
            minutesBefore = 15,
            enabled = true
        )

        val reminder = database.reminderQueries.selectAllReminders().executeAsOne()
        assertEquals(
            expected = dummyReminderWithLocation,
            actual = reminder
        )
    }

    @Test
    fun deleteReminder_valid() = suspendTest {
        database.insertDummyLocation()
        database.insertDummyReminder()

        repository.deleteReminder(1)

        val reminders = database.reminderQueries.selectAllReminders().executeAsList()
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun updateReminder_valid() = suspendTest {
        database.insertDummyLocation()
        database.insertDummyReminder()

        repository.updateReminder(
            id = 1,
            minutesBefore = 30,
            enabled = false
        )

        val reminder = database.reminderQueries.selectAllReminders().executeAsOne()
        assertEquals(
            expected = dummyReminderWithLocation.copy(
                minutesBefore = 30,
                enabled = false
            ),
            actual = reminder
        )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}

private val dummyReminder = Reminder(
    id = 1,
    locationId = 1,
    type = ReminderType.Sunset,
    minutesBefore = 15,
    enabled = true
)

private val dummyReminderWithLocation = ReminderWithLocation(
    id = 1,
    locationId = 1,
    locationLabel = "Test Location 1",
    locationLatitude = 42.3956001,
    locationLongitude = -71.1387674,
    locationTimeZone = "America/New_York",
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

