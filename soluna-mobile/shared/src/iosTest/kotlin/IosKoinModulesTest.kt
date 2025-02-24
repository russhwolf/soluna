package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.check.checkModules
import kotlin.test.AfterTest
import kotlin.test.Test

class IosKoinModulesTest {
    private val testAppModule = module {
    }

    @Test
    fun checkModules() {
        initKoin(testAppModule).checkModules {
            create<LocationDetailViewModel> { parametersOf(1L) }
        }
    }

    @Test
    fun swiftKotlinBridge() {
        SwiftKotlinBridge.initKoin()

        SwiftKotlinBridge.getHomeViewModel()
        SwiftKotlinBridge.getLocationListViewModel()
        SwiftKotlinBridge.getAddLocationViewModel()
        SwiftKotlinBridge.getLocationDetailViewModel(1L)
        SwiftKotlinBridge.getReminderListViewModel()

        SwiftKotlinBridge.getReminderNotificationList {}
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }
}
