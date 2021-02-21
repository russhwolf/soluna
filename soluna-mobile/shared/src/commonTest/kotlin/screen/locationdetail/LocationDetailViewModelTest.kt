package com.russhwolf.soluna.mobile.screen.locationdetail

import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.repository.configureMockLocationData
import com.russhwolf.soluna.mobile.repository.configureMockReminders
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import com.russhwolf.soluna.mobile.suspendTest
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.Test

class LocationDetailViewModelTest : AbstractViewModelTest<LocationDetailViewModel, LocationDetailViewState>() {
    private var locations: Array<Location> = emptyArray()
    private var reminders: Array<Reminder> = emptyArray()
    private val driver = createInMemorySqlDriver()
    private val database = createDatabase(driver)
    private val locationRepository by lazy {
        database.configureMockLocationData(*locations)
        LocationRepository.Impl(database, Dispatchers.Unconfined)
    }
    private val reminderRepository by lazy {
        database.configureMockReminders(*reminders)
        ReminderRepository.Impl(database, Dispatchers.Unconfined)
    }

    override suspend fun createViewModel(): LocationDetailViewModel =
        LocationDetailViewModel(1, locationRepository, reminderRepository, Dispatchers.Unconfined)

    @Test
    fun initialState_empty() = suspendTest {
        awaitLoading()
        assertState(LocationDetailViewState(location = null))
    }

    @Test
    fun initialState_populated() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        val reminder1 = Reminder(1, 2, ReminderType.Sunset, 15, true)
        val reminder2 = Reminder(2, 1, ReminderType.Sunset, 15, true)
        locations = arrayOf(location)
        reminders = arrayOf(reminder1, reminder2)
        awaitLoading()

        assertState(
            LocationDetailViewState(
                location = location,
                reminders = listOf(reminder2)
            )
        )
    }

    @Test
    fun updateLabel() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        awaitLoading()

        viewModel.setLabel("Updated").join()

        assertState(
            LocationDetailViewState(
                location = location.copy(label = "Updated")
            )
        )
    }

    @Test
    fun deleteLocation() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        awaitLoading()

        viewModel.delete().join()


        assertState(
            LocationDetailViewState(
                location = null,
                exitTrigger = EventTrigger.create()
            )
        )
    }

    @Test
    fun addReminder() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        awaitLoading()

        viewModel.addReminder(type = ReminderType.Sunset, minutesBefore = 15).join()

        val expectedReminder = Reminder(
            id = 1,
            locationId = 1,
            type = ReminderType.Sunset,
            minutesBefore = 15,
            enabled = true
        )

        assertState(
            LocationDetailViewState(
                location = location,
                reminders = listOf(expectedReminder)
            )
        )
    }

    @Test
    fun deleteReminder() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        val reminder = Reminder(1, 1, ReminderType.Sunset, 15, true)
        locations = arrayOf(location)
        reminders = arrayOf(reminder)
        awaitLoading()

        viewModel.deleteReminder(reminderId = 1).join()

        assertState(LocationDetailViewState(location = location))
    }

    @Test
    fun updateReminderEnabled() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        val reminder = Reminder(1, 1, ReminderType.Sunset, 15, true)
        locations = arrayOf(location)
        reminders = arrayOf(reminder)
        awaitLoading()

        viewModel.setReminderEnabled(reminderId = 1, enabled = false).join()

        assertState(
            LocationDetailViewState(
                location = location,
                reminders = listOf(reminder.copy(enabled = false))
            )
        )
    }

    @Test
    fun updateReminderMinutesBefore() = suspendTest {
        val location = Location(1, "Home", 27.18, 62.83, "UTC")
        val reminder = Reminder(1, 1, ReminderType.Sunset, 15, true)
        locations = arrayOf(location)
        reminders = arrayOf(reminder)
        awaitLoading()

        viewModel.setReminderMinutesBefore(reminderId = 1, minutesBefore = 20).join()

        assertState(
            LocationDetailViewState(
                location = location,
                reminders = listOf(reminder.copy(minutesBefore = 20))
            )
        )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
