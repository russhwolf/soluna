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

    fun getReminders(): Flow<List<Reminder>>

    suspend fun addReminder(type: ReminderType, minutesBefore: Int, enabled: Boolean = true)

    suspend fun deleteReminder(id: Long)

    suspend fun updateReminder(
        id: Long,
        minutesBefore: Int? = null,
        enabled: Boolean? = null,
        type: ReminderType? = null
    )

    class Impl(
        private val database: SolunaDb,
        private val backgroundDispatcher: CoroutineDispatcher
    ) : ReminderRepository {

        override fun getReminders(): Flow<List<Reminder>> =
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

        override suspend fun updateReminder(id: Long, minutesBefore: Int?, enabled: Boolean?, type: ReminderType?) =
            database.updateReminder(id, minutesBefore, enabled, type)

        private suspend fun SolunaDb.updateReminder(
            id: Long,
            minutesBefore: Int?,
            enabled: Boolean?,
            type: ReminderType?
        ) =
            withContext(backgroundDispatcher) {
                transaction {
                    if (minutesBefore != null) {
                        reminderQueries.updateReminderMinutesBeforeById(minutesBefore, id)
                    }
                    if (enabled != null) {
                        reminderQueries.updateReminderEnabledById(enabled, id)
                    }
                    if (type != null) {
                        reminderQueries.updateReminderTypeById(type, id)
                    }
                }
            }
    }
}
