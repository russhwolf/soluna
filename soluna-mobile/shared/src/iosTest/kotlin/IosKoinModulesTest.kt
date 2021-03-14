package com.russhwolf.soluna.mobile

import org.koin.dsl.module
import kotlin.test.Test

class IosKoinModulesTest {
    private val testAppModule = module {}

    @Test
    fun checkModules() {
        koinModulesTest(testAppModule)
    }
}
