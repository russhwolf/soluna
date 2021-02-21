package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
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
        database.insertDummyReminder()

        val reminders = repository.getReminders()

        assertEquals(1, reminders.size)
        assertEquals(
            expected = dummyReminder,
            actual = reminders[0]
        )
    }

    @Test
    fun getRemindersFlow() = suspendTest {
        database.insertDummyReminder()

        repository.getRemindersFlow().test {
            assertEquals(listOf(dummyReminder), expectItem())
            expectNoEvents()

            database.reminderQueries.deleteReminderById(1)
            assertEquals(emptyList(), expectItem())
            expectNoEvents()

            database.insertDummyReminder()
            assertEquals(listOf(dummyReminder.copy(id = 2)), expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun addReminder_valid() = suspendTest {

        repository.addReminder(
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
    fun deleteReminder_valid() = suspendTest {
        database.insertDummyReminder()

        repository.deleteReminder(1)

        val reminders = database.reminderQueries.selectAllReminders().executeAsList()
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun updateReminder_valid() = suspendTest {
        database.insertDummyReminder()

        repository.updateReminder(
            id = 1,
            minutesBefore = 30,
            enabled = false
        )

        val reminder = database.reminderQueries.selectAllReminders().executeAsOne()
        assertEquals(
            expected = dummyReminder.copy(
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
    type = ReminderType.Sunset,
    minutesBefore = 15,
    enabled = true
)

private fun SolunaDb.insertDummyReminder() {
    reminderQueries.insertReminder(
        type = ReminderType.Sunset,
        minutesBefore = 15,
        enabled = true
    )
}

