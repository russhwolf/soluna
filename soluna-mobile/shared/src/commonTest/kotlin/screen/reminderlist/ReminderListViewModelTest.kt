package com.russhwolf.soluna.mobile.screen.reminderlist

import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.repository.configureMockReminders
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.Test

class ReminderListViewModelTest : AbstractViewModelTest<ReminderListViewModel, ReminderListViewState>() {
    private var reminders: Array<Reminder> = emptyArray()
    private val driver = createInMemorySqlDriver()
    private val database = createDatabase(driver)
    private val reminderRepository by lazy {
        database.configureMockReminders(*reminders)
        ReminderRepository.Impl(database, Dispatchers.Unconfined)
    }

    override suspend fun createViewModel(): ReminderListViewModel =
        ReminderListViewModel(reminderRepository, Dispatchers.Unconfined)

    @Test
    fun initialState_empty() = suspendTest {
        awaitLoading()
        assertState(ReminderListViewState())
    }

    @Test
    fun initialState_populated() = suspendTest {
        val reminder1 = Reminder(1, ReminderType.Sunrise, 15, true)
        val reminder2 = Reminder(2, ReminderType.Sunset, 15, true)
        reminders = arrayOf(reminder1, reminder2)
        awaitLoading()

        assertState(ReminderListViewState(reminders = reminders.toList()))
    }

    @Test
    fun addReminder() = suspendTest {
        awaitLoading()

        viewModel.addReminder(type = ReminderType.Sunset, minutesBefore = 15).join()

        val expectedReminder = Reminder(
            id = 1,
            type = ReminderType.Sunset,
            minutesBefore = 15,
            enabled = true
        )

        assertState(ReminderListViewState(reminders = listOf(expectedReminder)))
    }

    @Test
    fun deleteReminder() = suspendTest {
        val reminder = Reminder(1, ReminderType.Sunset, 15, true)
        reminders = arrayOf(reminder)
        awaitLoading()

        viewModel.deleteReminder(reminderId = 1).join()

        assertState(ReminderListViewState())
    }

    @Test
    fun updateReminderEnabled() = suspendTest {
        val reminder = Reminder(1, ReminderType.Sunset, 15, true)
        reminders = arrayOf(reminder)
        awaitLoading()

        viewModel.setReminderEnabled(reminderId = 1, enabled = false).join()

        assertState(ReminderListViewState(reminders = listOf(reminder.copy(enabled = false))))
    }

    @Test
    fun updateReminderMinutesBefore() = suspendTest {
        val reminder = Reminder(1, ReminderType.Sunset, 15, true)
        reminders = arrayOf(reminder)
        awaitLoading()

        viewModel.setReminderMinutesBefore(reminderId = 1, minutesBefore = 20).join()

        assertState(ReminderListViewState(reminders = listOf(reminder.copy(minutesBefore = 20))))
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
