package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.asListFlow
import com.russhwolf.soluna.mobile.util.runInBackground
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    suspend fun getReminders(): List<ReminderWithLocation>

    fun getRemindersFlow(): Flow<List<ReminderWithLocation>>

    suspend fun getRemindersForLocation(locationId: Long): List<ReminderWithLocation>

    fun getRemindersForLocationFlow(locationId: Long): Flow<List<ReminderWithLocation>>

    suspend fun addReminder(locationId: Long, type: ReminderType, minutesBefore: Int, enabled: Boolean)

    suspend fun deleteReminder(id: Long)

    suspend fun updateReminder(id: Long, minutesBefore: Int? = null, enabled: Boolean? = null)

    class Impl(private val database: SolunaDb) : ReminderRepository {

        override suspend fun getReminders(): List<ReminderWithLocation> =
            database.getReminders()

        private suspend fun SolunaDb.getReminders(): List<ReminderWithLocation> = runInBackground {
            reminderQueries.selectAllReminders().executeAsList()
        }

        override fun getRemindersFlow(): Flow<List<ReminderWithLocation>> =
            database.reminderQueries
                .selectAllReminders()
                .asListFlow()

        override suspend fun getRemindersForLocation(locationId: Long): List<ReminderWithLocation> =
            database.getRemindersForLocation(locationId)

        private suspend fun SolunaDb.getRemindersForLocation(locationId: Long): List<ReminderWithLocation> =
            runInBackground {
                reminderQueries.selectRemindersByLocationId(locationId).executeAsList()
            }

        override fun getRemindersForLocationFlow(locationId: Long): Flow<List<ReminderWithLocation>> =
            database.reminderQueries
                .selectRemindersByLocationId(locationId)
                .asListFlow()

        override suspend fun addReminder(locationId: Long, type: ReminderType, minutesBefore: Int, enabled: Boolean) =
            database.addReminder(locationId, type, minutesBefore, enabled)

        private suspend fun SolunaDb.addReminder(
            locationId: Long,
            type: ReminderType,
            minutesBefore: Int,
            enabled: Boolean
        ) = runInBackground {
            reminderQueries
                .insertReminder(locationId, type, minutesBefore, enabled)
        }

        override suspend fun deleteReminder(id: Long) = database.deleteReminder(id)

        private suspend fun SolunaDb.deleteReminder(id: Long) = runInBackground {
            reminderQueries
                .deleteReminderById(id)
        }

        override suspend fun updateReminder(id: Long, minutesBefore: Int?, enabled: Boolean?) =
            database.updateReminder(id, minutesBefore, enabled)

        private suspend fun SolunaDb.updateReminder(id: Long, minutesBefore: Int?, enabled: Boolean?) =
            runInBackground {
                transaction {
                    if (minutesBefore != null) {
                        reminderQueries.updateReminderMinutesBeforeById(minutesBefore, id)
                    }
                    if (enabled != null) {
                        reminderQueries.updateReminderEnabledById(enabled, id)
                    }
                }
            }
    }
}
