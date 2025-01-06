package com.russhwolf.soluna.mobile.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.sqldelight.Reminder
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

class ReminderRepository(
    private val database: SolunaDb,
    private val backgroundDispatcher: CoroutineDispatcher
) {

    fun getReminders(): Flow<List<Reminder>> =
        database.reminderQueries
            .selectAllReminders()
            .asFlow()
            .mapToList(backgroundDispatcher)
            .distinctUntilChanged()

    suspend fun addReminder(type: ReminderType, minutesBefore: Int, enabled: Boolean = true) =
        withContext(backgroundDispatcher) {
            database.reminderQueries
                .insertReminder(type, minutesBefore, enabled)
        }

    suspend fun deleteReminder(id: Long) =
        withContext(backgroundDispatcher) {
            database.reminderQueries
                .deleteReminderById(id)
        }

    suspend fun updateReminder(
        id: Long,
        minutesBefore: Int? = null,
        enabled: Boolean? = null,
        type: ReminderType? = null
    ) =
        withContext(backgroundDispatcher) {
            database.transaction {
                if (minutesBefore != null) {
                    database.reminderQueries.updateReminderMinutesBeforeById(minutesBefore, id)
                }
                if (enabled != null) {
                    database.reminderQueries.updateReminderEnabledById(enabled, id)
                }
                if (type != null) {
                    database.reminderQueries.updateReminderTypeById(type, id)
                }
            }
        }

}
