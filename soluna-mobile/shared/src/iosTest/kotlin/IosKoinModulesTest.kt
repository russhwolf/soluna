package com.russhwolf.soluna.mobile

import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.Test

class IosKoinModulesTest {
    private val testAppModule = module {
    }

    @Test
    fun checkModules() {
        koinModulesTest(testAppModule)
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

        stopKoin()
    }
}
