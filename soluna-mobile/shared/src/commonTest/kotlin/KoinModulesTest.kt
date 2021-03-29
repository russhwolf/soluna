package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.test.check.checkModules

fun koinModulesTest(appModule: Module) {
    val koinApplication = initKoin(appModule)

    koinApplication.koin.checkModules {
        create<LocationDetailViewModel> { parametersOf(1L) }
    }

    stopKoin()
}
