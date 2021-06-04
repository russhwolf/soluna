package com.russhwolf.soluna.android

import android.app.Application
import android.content.Context
import com.russhwolf.soluna.mobile.initKoin
import com.russhwolf.soluna.mobile.koinUiScopeQualifier
import com.russhwolf.soluna.mobile.repository.AndroidDeviceLocationService
import com.russhwolf.soluna.mobile.repository.DeviceLocationService
import com.russhwolf.soluna.mobile.repository.ReminderNotificationRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.dsl.module

class SolunaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val koinApplication = initKoin(module {
            single<Context> { applicationContext }

            scope(koinUiScopeQualifier) {
                scoped<DeviceLocationService> { AndroidDeviceLocationService(applicationContext, get()) }
            }
        })

        koinApplication.koin.get<ReminderNotificationRepository>().getUpcomingNotifications()
            .onEach { reminderNotifications ->
                println("Reminder Notifications dirtied!\n$reminderNotifications")
            }
            .launchIn(GlobalScope)
    }
}
