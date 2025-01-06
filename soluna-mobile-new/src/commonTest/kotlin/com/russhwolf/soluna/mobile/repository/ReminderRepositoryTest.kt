package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.invoke
import com.russhwolf.soluna.mobile.db.sqldelight.Reminder
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb
import com.russhwolf.soluna.mobile.test.createInMemorySqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReminderRepositoryTest {

    private val driver = createInMemorySqlDriver()
    private val database = SolunaDb(driver)
    private val repository = ReminderRepository(database, Dispatchers.Unconfined)

    @Test
    fun getReminders() = runTest {
        repository.getReminders().test {
            assertEquals(emptyList(), awaitItem())
            expectNoEvents()

            database.insertDummyReminder()

            assertEquals(listOf(dummyReminder), awaitItem())
            expectNoEvents()

            database.reminderQueries.deleteReminderById(1)
            assertEquals(emptyList(), awaitItem())
            expectNoEvents()

            database.insertDummyReminder()
            assertEquals(listOf(dummyReminder.copy(id = 2)), awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun addReminder_valid() = runTest {

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
    fun deleteReminder_valid() = runTest {
        database.insertDummyReminder()

        repository.deleteReminder(1)

        val reminders = database.reminderQueries.selectAllReminders().executeAsList()
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun updateReminder_valid() = runTest {
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

