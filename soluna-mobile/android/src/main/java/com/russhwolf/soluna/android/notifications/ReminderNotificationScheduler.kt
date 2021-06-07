package com.russhwolf.soluna.android.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.soluna.android.extensions.text
import com.russhwolf.soluna.android.extensions.toDisplayTime
import com.russhwolf.soluna.mobile.repository.ReminderNotification
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone


class ReminderNotificationScheduler(context: Context) {

    companion object {
        private const val CHANNEL_ID = "ReminderNotifications"

        fun createNotificationChannel(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context.applicationContext)
            val notificationChannel =
                NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .setName("Reminder Notifications") // TODO localize
                    .build()
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private val context = context.applicationContext
    private val alarmManager = context.getSystemService<AlarmManager>() ?: error("Failed to retrieve alarm manager!")

    private val Context.dataStore by preferencesDataStore("reminderNotifications")
    private val dataStore get() = context.dataStore
    private val existingAlarmsKey = stringSetPreferencesKey("existingAlarms")

    suspend fun scheduleNotifications(reminderNotifications: List<ReminderNotification>) {
        clearPendingNotifications()
        reminderNotifications.forEach { scheduleNotification(it) }
    }

    suspend fun clearPendingNotifications() {
        dataStore.data.first()[existingAlarmsKey].orEmpty().forEach { requestCode ->
            val intent = PendingIntent.getBroadcast(
                context,
                requestCode.toIntOrNull() ?: 0,
                Intent(context, NotificationBroadcastReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE + PendingIntent.FLAG_IMMUTABLE
            )
            if (intent != null) {
                alarmManager.cancel(intent)
            }
        }
    }

    private suspend fun scheduleNotification(reminderNotification: ReminderNotification) {
        val eventType = reminderNotification.type.text
        val locationLabel = reminderNotification.locationLabel
        val eventTime = reminderNotification.eventTime.toDisplayTime(TimeZone.of(reminderNotification.timeZone))

        val showRequestCode = (reminderNotification to true).hashCode()
        val showPendingIntent = PendingIntent.getBroadcast(
            context,
            showRequestCode,
            NotificationBroadcastReceiver.getShowNotificationIntent(
                context = context,
                notificationId = reminderNotification.hashCode(),
                message = "Upcoming $eventType in $locationLabel will be at $eventTime",
                channel = CHANNEL_ID
            ),
            PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )

        val hideRequestCode = (reminderNotification to false).hashCode()
        val hidePendingIntent = PendingIntent.getBroadcast(
            context,
            hideRequestCode,
            NotificationBroadcastReceiver.getHideNotificationIntent(
                context = context,
                notificationId = reminderNotification.hashCode()
            ),
            PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )

        dataStore.edit {
            it[existingAlarmsKey] =
                it[existingAlarmsKey].orEmpty() + setOf(showRequestCode.toString(), hideRequestCode.toString())
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderNotification.notificationTime.toEpochMilliseconds(),
            showPendingIntent
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderNotification.eventTime.toEpochMilliseconds(),
            hidePendingIntent
        )
    }

}
