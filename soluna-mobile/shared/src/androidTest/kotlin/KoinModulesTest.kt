package com.russhwolf.soluna.mobile

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class KoinModulesTest {
    private val testAppModule = module {
        single<Context> { ApplicationProvider.getApplicationContext() }
    }

    @Test
    fun checkModules() {
        testCheckModules(testAppModule)
    }
}
