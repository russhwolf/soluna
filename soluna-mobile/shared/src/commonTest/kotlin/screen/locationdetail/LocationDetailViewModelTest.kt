package com.russhwolf.soluna.mobile.screen.locationdetail

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.repository.MockLocationRepository
import com.russhwolf.soluna.mobile.repository.MockReminderRepository
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import com.russhwolf.soluna.mobile.suspendTest
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test


class LocationDetailViewModelTest : AbstractViewModelTest<LocationDetailViewModel, LocationDetailViewState>() {
    private var locations: Array<Location> = emptyArray()
    private var reminders: Array<Reminder> = emptyArray()
    private val locationRepository by lazy { MockLocationRepository(*locations) }
    private val reminderRepository by lazy { MockReminderRepository(locationRepository, *reminders) }

    override suspend fun createViewModel(): LocationDetailViewModel =
        LocationDetailViewModel(0, locationRepository, reminderRepository, Dispatchers.Unconfined)

    @Test
    fun initialState_empty() = suspendTest {
        awaitLoading()
        assertState(LocationDetailViewState(location = null))
    }

    @Test
    fun initialState_populated() = suspendTest {
        val location = Location.Impl(0, "Home", 27.18, 62.83, "UTC")
        val reminder1 = Reminder.Impl(0, 1, ReminderType.Sunset, 15, true)
        val reminder2 = Reminder.Impl(1, 0, ReminderType.Sunset, 15, true)
        locations = arrayOf(location)
        reminders = arrayOf(reminder1, reminder2)
        awaitLoading()

        assertState(
            LocationDetailViewState(
                location = location,
                reminders = listOf(reminder2.withLocation(location))
            )
        )
    }

    @Test
    fun updateLabel() = suspendTest {
        val location = Location.Impl(0, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        awaitLoading()

        viewModel.setLabel("Updated")

        assertState(
            LocationDetailViewState(
                location = location.copy(label = "Updated")
            )
        )
    }

    @Test
    fun deleteLocation() = suspendTest {
        val location = Location.Impl(0, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        awaitLoading()

        viewModel.delete()

        assertState(
            LocationDetailViewState(
                location = null,
                exitTrigger = EventTrigger.create()
            )
        )
    }

    @Test
    fun addReminder() = suspendTest {
        val location = Location.Impl(0, "Home", 27.18, 62.83, "UTC")
        locations = arrayOf(location)
        awaitLoading()

        viewModel.addReminder(type = ReminderType.Sunset, minutesBefore = 15)
        val expectedReminder = Reminder.Impl(
            id = 0,
            locationId = 0,
            type = ReminderType.Sunset,
            minutesBefore = 15,
            enabled = true
        )

        assertState(
            LocationDetailViewState(
                location = location,
                reminders = listOf(expectedReminder.withLocation(location))
            )
        )
    }

    @Test
    fun deleteReminder() = suspendTest {
        val location = Location.Impl(0, "Home", 27.18, 62.83, "UTC")
        val reminder = Reminder.Impl(0, 0, ReminderType.Sunset, 15, true)
        locations = arrayOf(location)
        reminders = arrayOf(reminder)
        awaitLoading()

        viewModel.deleteReminder(reminderId = 0)

        assertState(LocationDetailViewState(location = location))
    }

    @Test
    fun updateReminderEnabled() = suspendTest {
        val location = Location.Impl(0, "Home", 27.18, 62.83, "UTC")
        val reminder = Reminder.Impl(0, 0, ReminderType.Sunset, 15, true)
        locations = arrayOf(location)
        reminders = arrayOf(reminder)
        awaitLoading()

        viewModel.setReminderEnabled(reminderId = 0, enabled = false)

        assertState(
            LocationDetailViewState(
                location = location,
                reminders = listOf(reminder.withLocation(location).copy(enabled = false))
            )
        )
    }

    @Test
    fun updateReminderMinutesBefore() = suspendTest {
        val location = Location.Impl(0, "Home", 27.18, 62.83, "UTC")
        val reminder = Reminder.Impl(0, 0, ReminderType.Sunset, 15, true)
        locations = arrayOf(location)
        reminders = arrayOf(reminder)
        awaitLoading()

        viewModel.setReminderMinutesBefore(reminderId = 0, minutesBefore = 20)

        assertState(
            LocationDetailViewState(
                location = location,
                reminders = listOf(reminder.withLocation(location).copy(minutesBefore = 20))
            )
        )
    }
}

private fun Reminder.withLocation(location: Location) =
    if (locationId == location.id) {
        ReminderWithLocation.Impl(
            id,
            location.id,
            location.label,
            type,
            minutesBefore,
            enabled
        )
    } else error("Mismatched location id! reminder.locationId=$locationId, location.id=${location.id}")
