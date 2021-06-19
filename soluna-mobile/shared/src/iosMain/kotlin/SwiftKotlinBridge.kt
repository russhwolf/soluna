@file:Suppress("unused") // This object is called from Swift

package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.repository.ReminderNotification
import com.russhwolf.soluna.mobile.repository.ReminderNotificationRepository
import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.home.HomeViewModel
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import com.russhwolf.soluna.mobile.screen.reminderlist.ReminderListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toNSDateComponents
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import platform.Foundation.NSTimeZone

object SwiftKotlinBridge : KoinComponent {
    private const val scopeId = "swiftKotlinBridge"

    private val coroutineScope = MainScope()

    fun initKoin() {
        val appModule = module {
            scope(koinUiScopeQualifier) {}
        }
        val koinApplication = initKoin(appModule)
        koinApplication.koin.createScope(scopeId, koinUiScopeQualifier)
    }

    fun getHomeViewModel() = getKoin().getScope(scopeId).get<HomeViewModel>()
    fun getLocationListViewModel() = getKoin().getScope(scopeId).get<LocationListViewModel>()
    fun getAddLocationViewModel() = getKoin().getScope(scopeId).get<AddLocationViewModel>()
    fun getLocationDetailViewModel(id: Long) =
        getKoin().getScope(scopeId).get<LocationDetailViewModel> { parametersOf(id) }

    fun getReminderListViewModel() = getKoin().getScope(scopeId).get<ReminderListViewModel>()

    fun observeReminderNotificationList(onEach: (List<ReminderNotification>?) -> Unit) {
        coroutineScope.launch(Dispatchers.Main) {
            get<ReminderNotificationRepository>().getUpcomingNotifications()
                .onEach { onEach(it) }
                .collect()
        }
    }

    fun getReminderNotificationList(onSuccess: (List<ReminderNotification>?) -> Unit) {
        coroutineScope.launch(Dispatchers.Main) {
            val reminders = get<ReminderNotificationRepository>().getUpcomingNotifications().first()
            onSuccess(reminders)
        }
    }

    fun nsDateComponents(instant: Instant, timeZone: NSTimeZone) =
        instant.toLocalDateTime(timeZone.toKotlinTimeZone()).toNSDateComponents()
}
