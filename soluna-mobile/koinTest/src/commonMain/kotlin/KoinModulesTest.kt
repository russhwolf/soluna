package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.test.check.checkModules

fun testCheckModules(appModule: Module) {
    initKoin(appModule)

    KoinContextHandler.get().checkModules {
        create<LocationDetailViewModel> { parametersOf(1L) }
    }

    stopKoin()
}
