package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.SolunaDb

fun SolunaDb.configureMockReminders(vararg reminders: Reminder) = transaction {
    reminders.forEach { reminderQueries.insertReminder(it.locationId, it.type, it.minutesBefore, it.enabled) }
}
