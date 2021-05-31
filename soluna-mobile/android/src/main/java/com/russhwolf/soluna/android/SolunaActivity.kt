package com.russhwolf.soluna.android

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.russhwolf.soluna.android.ui.SolunaApp
import com.russhwolf.soluna.mobile.initKoin
import com.russhwolf.soluna.mobile.repository.ReminderNotificationRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class SolunaActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val koinApplication = initKoin(module {
            single<Context> { applicationContext }
            single<ComponentActivity> { this@SolunaActivity }
        })

        koinApplication.koin.get<ReminderNotificationRepository>().getUpcomingNotifications()
            .onEach { reminderNotifications ->
                println("Reminder Notifications dirtied!\n$reminderNotifications")
            }
            .launchIn(lifecycleScope)

        setContent {
            SolunaApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }
}
