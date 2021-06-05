package com.russhwolf.soluna.android.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.russhwolf.soluna.mobile.repository.ReminderNotification
import com.russhwolf.soluna.mobile.repository.ReminderNotificationRepository
import kotlinx.coroutines.flow.first
import java.time.Duration

class ScheduleReminderNotificationsWorker(
    appContext: Context,
    params: WorkerParameters,
    private val reminderNotificationRepository: ReminderNotificationRepository,
    private val reminderNotificationScheduler: ReminderNotificationScheduler
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val UNIQUE_NAME = "ScheduleReminderNotifications"

        fun scheduleReminders(context: Context, reminderNotifications: List<ReminderNotification>?) {
            if (reminderNotifications != null) {
                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(
                        UNIQUE_NAME,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        PeriodicWorkRequestBuilder<ScheduleReminderNotificationsWorker>(Duration.ofDays(1))
                            .build()
                    )
            } else {
                WorkManager.getInstance(context)
                    .cancelUniqueWork(UNIQUE_NAME)
            }
        }
    }

    override suspend fun doWork(): Result {
        val reminderNotifications = reminderNotificationRepository.getUpcomingNotifications().first()
            ?: return Result.failure()

        reminderNotificationScheduler.scheduleNotifications(reminderNotifications)
        return Result.success()
    }
}

