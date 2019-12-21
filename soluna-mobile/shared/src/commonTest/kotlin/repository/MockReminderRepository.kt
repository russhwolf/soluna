package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.runBlocking
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class MockReminderRepository(
    private val locationRepository: LocationRepository,
    vararg reminders: Reminder
) : ReminderRepository {
    private var nextReminderId = 0L
    private val reminders = reminders.toMutableList()

    private val reminderListeners = mutableListOf<() -> Unit>()

    override suspend fun getReminders(): List<ReminderWithLocation> =
        reminders
            .map { reminder ->
                ReminderWithLocation.Impl(
                    reminder.id,
                    reminder.locationId,
                    locationRepository.getLocations().first { it.id == reminder.locationId }.label,
                    reminder.type,
                    reminder.minutesBefore,
                    reminder.enabled
                )
            }

    override fun getRemindersFlow(): Flow<List<ReminderWithLocation>> = callbackFlow {
        val listener: () -> Unit = { offer(runBlocking { getReminders() }) }

        reminderListeners.add(listener)
        awaitClose {
            reminderListeners.remove(listener)
        }
    }.distinctUntilChanged()

    override suspend fun getRemindersForLocation(locationId: Long): List<ReminderWithLocation> =
        reminders
            .filter { it.locationId == locationId }
            .map { reminder ->
                ReminderWithLocation.Impl(
                    reminder.id,
                    reminder.locationId,
                    locationRepository.getLocations().first { it.id == reminder.locationId }.label,
                    reminder.type,
                    reminder.minutesBefore,
                    reminder.enabled
                )
            }

    override fun getRemindersForLocationFlow(locationId: Long): Flow<List<ReminderWithLocation>> =
        callbackFlow {
            val listener: () -> Unit = { offer(runBlocking { getRemindersForLocation(locationId) }) }

            reminderListeners.add(listener)
            awaitClose {
                reminderListeners.remove(listener)
            }
        }.distinctUntilChanged()

    override suspend fun addReminder(locationId: Long, type: ReminderType, minutesBefore: Int, enabled: Boolean) {
        reminders.add(Reminder.Impl(nextReminderId++, locationId, type, minutesBefore, enabled))
        reminderListeners.forEach { it() }
    }

    override suspend fun deleteReminder(id: Long) {
        reminders.removeAll { it.id == id }
        reminderListeners.forEach { it() }
    }

    override suspend fun updateReminder(id: Long, minutesBefore: Int?, enabled: Boolean?) {
        val index = reminders.indexOfFirst { it.id == id }
        if (index < 0) return

        val prevReminder = reminders[index]
        reminders[index] =
            Reminder.Impl(
                id,
                prevReminder.locationId,
                prevReminder.type,
                minutesBefore ?: prevReminder.minutesBefore,
                enabled ?: prevReminder.enabled
            )
        reminderListeners.forEach { it() }
    }
}
