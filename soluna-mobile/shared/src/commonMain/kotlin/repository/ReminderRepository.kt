package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.ReminderWithLocation
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ReminderRepository {

    suspend fun getReminders(): List<ReminderWithLocation>

    fun getRemindersFlow(): Flow<List<ReminderWithLocation>>

    suspend fun getRemindersForLocation(locationId: Long): List<Reminder>

    fun getRemindersForLocationFlow(locationId: Long): Flow<List<Reminder>>

    suspend fun addReminder(locationId: Long, type: ReminderType, minutesBefore: Int, enabled: Boolean)

    suspend fun deleteReminder(id: Long)

    suspend fun updateReminder(id: Long, minutesBefore: Int? = null, enabled: Boolean? = null)

    class Impl(
        private val database: SolunaDb,
        private val backgroundDispatcher: CoroutineDispatcher
    ) : ReminderRepository {

        override suspend fun getReminders(): List<ReminderWithLocation> =
            database.getReminders()

        private suspend fun SolunaDb.getReminders(): List<ReminderWithLocation> = withContext(backgroundDispatcher) {
            reminderQueries.selectAllReminders().executeAsList()
        }

        override fun getRemindersFlow(): Flow<List<ReminderWithLocation>> =
            database.reminderQueries
                .selectAllReminders()
                .asFlow()
                .mapToList(backgroundDispatcher)

        override suspend fun getRemindersForLocation(locationId: Long): List<Reminder> =
            database.getRemindersForLocation(locationId)

        private suspend fun SolunaDb.getRemindersForLocation(locationId: Long): List<Reminder> =
            withContext(backgroundDispatcher) {
                reminderQueries.selectRemindersByLocationId(locationId).executeAsList()
            }

        override fun getRemindersForLocationFlow(locationId: Long): Flow<List<Reminder>> =
            database.reminderQueries
                .selectRemindersByLocationId(locationId)
                .asFlow()
                .mapToList(backgroundDispatcher)

        override suspend fun addReminder(locationId: Long, type: ReminderType, minutesBefore: Int, enabled: Boolean) =
            database.addReminder(locationId, type, minutesBefore, enabled)

        private suspend fun SolunaDb.addReminder(
            locationId: Long,
            type: ReminderType,
            minutesBefore: Int,
            enabled: Boolean
        ) = withContext(backgroundDispatcher) {
            reminderQueries
                .insertReminder(locationId, type, minutesBefore, enabled)
        }

        override suspend fun deleteReminder(id: Long) = database.deleteReminder(id)

        private suspend fun SolunaDb.deleteReminder(id: Long) = withContext(backgroundDispatcher) {
            reminderQueries
                .deleteReminderById(id)
        }

        override suspend fun updateReminder(id: Long, minutesBefore: Int?, enabled: Boolean?) =
            database.updateReminder(id, minutesBefore, enabled)

        private suspend fun SolunaDb.updateReminder(id: Long, minutesBefore: Int?, enabled: Boolean?) =
            withContext(backgroundDispatcher) {
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
