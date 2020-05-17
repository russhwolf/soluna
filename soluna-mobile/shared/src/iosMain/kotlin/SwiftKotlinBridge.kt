@file:Suppress("unused")

package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

object SwiftKotlinBridge : KoinComponent {
    fun initKoin() {
        val appModule = module {

        }
        initKoin(appModule)
    }

    fun getLocationListViewModel(): LocationListViewModel = get()
    fun getAddLocationViewModel(): AddLocationViewModel = get()
    fun getLocationDetailViewModel(id: Long): LocationDetailViewModel = get { parametersOf(id) }
}
