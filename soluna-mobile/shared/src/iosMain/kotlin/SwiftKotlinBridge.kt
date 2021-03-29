@file:Suppress("unused")

package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.home.HomeViewModel
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import com.russhwolf.soluna.mobile.screen.reminderlist.ReminderListViewModel
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

    fun getHomeViewModel(): HomeViewModel = get()
    fun getLocationListViewModel(): LocationListViewModel = get()
    fun getAddLocationViewModel(): AddLocationViewModel = get()
    fun getLocationDetailViewModel(id: Long): LocationDetailViewModel = get { parametersOf(id) }
    fun getReminderListViewModel(): ReminderListViewModel = get()
}
