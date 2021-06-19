package com.russhwolf.soluna.android

import android.app.Application
import android.content.Context
import com.russhwolf.soluna.android.notifications.ReminderNotificationScheduler
import com.russhwolf.soluna.android.notifications.ScheduleReminderNotificationsWorker
import com.russhwolf.soluna.mobile.initKoin
import com.russhwolf.soluna.mobile.koinUiScopeQualifier
import com.russhwolf.soluna.mobile.repository.AndroidDeviceLocationService
import com.russhwolf.soluna.mobile.repository.DeviceLocationService
import com.russhwolf.soluna.mobile.repository.ReminderNotificationRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.dsl.module

class SolunaApplication : Application() {
    private val coroutineScope = MainScope()

    override fun onCreate() {
        super.onCreate()

        val reminderNotificationScheduler = ReminderNotificationScheduler(this)
        ReminderNotificationScheduler.createNotificationChannel(this)

        @OptIn(KoinExperimentalAPI::class)
        val koinApplication = initKoin(module {
            single<Context> { applicationContext }

            worker {
                ScheduleReminderNotificationsWorker(get(), get(), get(), reminderNotificationScheduler)
            }

            scope(koinUiScopeQualifier) {
                scoped<DeviceLocationService> { AndroidDeviceLocationService(applicationContext, get()) }
            }
        }) {
            workManagerFactory()
        }

        koinApplication.koin.get<ReminderNotificationRepository>().getUpcomingNotifications()
            .onEach { reminderNotifications ->
                ScheduleReminderNotificationsWorker.scheduleReminders(this, reminderNotifications)
            }
            .launchIn(coroutineScope)
    }
}
