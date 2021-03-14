package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

interface ReminderRepository {

    suspend fun getReminders(): List<Reminder>

    fun getRemindersFlow(): Flow<List<Reminder>>

    suspend fun addReminder(type: ReminderType, minutesBefore: Int, enabled: Boolean)

    suspend fun deleteReminder(id: Long)

    suspend fun updateReminder(id: Long, minutesBefore: Int? = null, enabled: Boolean? = null)

    class Impl(
        private val database: SolunaDb,
        private val backgroundDispatcher: CoroutineDispatcher
    ) : ReminderRepository {

        override suspend fun getReminders(): List<Reminder> =
            database.getReminders()

        private suspend fun SolunaDb.getReminders(): List<Reminder> = withContext(backgroundDispatcher) {
            reminderQueries.selectAllReminders().executeAsList()
        }

        override fun getRemindersFlow(): Flow<List<Reminder>> =
            database.reminderQueries
                .selectAllReminders()
                .asFlow()
                .mapToList(backgroundDispatcher)
                .distinctUntilChanged()

        override suspend fun addReminder(type: ReminderType, minutesBefore: Int, enabled: Boolean) =
            database.addReminder(type, minutesBefore, enabled)

        private suspend fun SolunaDb.addReminder(
            type: ReminderType,
            minutesBefore: Int,
            enabled: Boolean
        ) = withContext(backgroundDispatcher) {
            reminderQueries
                .insertReminder(type, minutesBefore, enabled)
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
