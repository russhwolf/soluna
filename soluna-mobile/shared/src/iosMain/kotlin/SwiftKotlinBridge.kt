@file:Suppress("unused") // This object is called from Swift

package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.home.HomeViewModel
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import com.russhwolf.soluna.mobile.screen.reminderlist.ReminderListViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toNSDate
import kotlinx.datetime.toNSTimeZone
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

object SwiftKotlinBridge : KoinComponent {
    fun initKoin() {
        val appModule = module {

        }
        initKoin(appModule)
    }

    fun getHomeViewModel() = NativeViewModel(get<HomeViewModel>())
    fun getLocationListViewModel() = NativeViewModel(get<LocationListViewModel>())
    fun getAddLocationViewModel() = NativeViewModel(get<AddLocationViewModel>())
    fun getLocationDetailViewModel(id: Long) = NativeViewModel(get<LocationDetailViewModel> { parametersOf(id) })
    fun getReminderListViewModel() = NativeViewModel(get<ReminderListViewModel>())

    fun nsDate(instant: Instant) = instant.toNSDate()
    fun nsTimeZone(timeZone: TimeZone) = timeZone.toNSTimeZone()
}
