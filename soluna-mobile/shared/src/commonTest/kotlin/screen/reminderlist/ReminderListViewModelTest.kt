package com.russhwolf.soluna.mobile.screen.reminderlist

import app.cash.turbine.test
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.repository.configureMockReminders
import com.russhwolf.soluna.mobile.screen.expectViewModelState
import com.russhwolf.soluna.mobile.screen.stateAndEvents
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ReminderListViewModelTest {
    private var reminders: Array<Reminder> = emptyArray()
    private val driver = createInMemorySqlDriver()
    private val database = createDatabase(driver)
    private val reminderRepository by lazy {
        database.configureMockReminders(*reminders)
        ReminderRepository.Impl(database, Dispatchers.Unconfined)
    }

    private val viewModel by lazy {
        ReminderListViewModel(reminderRepository, Dispatchers.Unconfined)
    }

    @Test
    fun initialState_empty() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(ReminderListViewModel.State(emptyList()), expectViewModelState())
        }
    }

    @Test
    fun initialState_populated() = suspendTest {
        val reminder1 = Reminder(1, ReminderType.Sunrise, 15, true)
        val reminder2 = Reminder(2, ReminderType.Sunset, 15, true)
        reminders = arrayOf(reminder1, reminder2)

        viewModel.stateAndEvents.test {
            assertEquals(ReminderListViewModel.State(reminders.toList()), expectViewModelState())
        }
    }

    @Test
    fun addReminder() = suspendTest {
        viewModel.stateAndEvents.test {
            assertEquals(ReminderListViewModel.State(emptyList()), expectViewModelState())
            expectNoEvents()

            viewModel.performAction(ReminderListViewModel.Action.AddReminder(ReminderType.Sunset, 15))
            val expectedReminder = Reminder(1, ReminderType.Sunset, 15, true)
            assertEquals(ReminderListViewModel.State(listOf(expectedReminder)), expectViewModelState())
        }
    }

    @Test
    fun deleteReminder() = suspendTest {
        val reminder = Reminder(1, ReminderType.Sunset, 15, true)
        reminders = arrayOf(reminder)

        viewModel.stateAndEvents.test {
            assertEquals(ReminderListViewModel.State(listOf(reminder)), expectViewModelState())
            expectNoEvents()

            viewModel.performAction(ReminderListViewModel.Action.RemoveReminder(1))
            assertEquals(ReminderListViewModel.State(emptyList()), expectViewModelState())
        }
    }

    @Test
    fun updateReminderEnabled() = suspendTest {
        val reminder = Reminder(1, ReminderType.Sunset, 15, true)
        reminders = arrayOf(reminder)

        viewModel.stateAndEvents.test {
            assertEquals(ReminderListViewModel.State(listOf(reminder)), expectViewModelState())
            expectNoEvents()

            viewModel.performAction(ReminderListViewModel.Action.SetReminderEnabled(1, false))
            assertEquals(ReminderListViewModel.State(listOf(reminder.copy(enabled = false))), expectViewModelState())
        }
    }

    @Test
    fun updateReminderMinutesBefore() = suspendTest {
        val reminder = Reminder(1, ReminderType.Sunset, 15, true)
        reminders = arrayOf(reminder)

        viewModel.stateAndEvents.test {
            assertEquals(ReminderListViewModel.State(listOf(reminder)), expectViewModelState())
            expectNoEvents()

            viewModel.performAction(ReminderListViewModel.Action.SetReminderMinutesBefore(1, 20))
            assertEquals(ReminderListViewModel.State(listOf(reminder.copy(minutesBefore = 20))), expectViewModelState())
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
