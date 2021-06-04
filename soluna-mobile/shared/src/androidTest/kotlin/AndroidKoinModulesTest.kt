package com.russhwolf.soluna.mobile

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.russhwolf.soluna.mobile.repository.DeviceLocationResult
import com.russhwolf.soluna.mobile.repository.DeviceLocationService
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class AndroidKoinModulesTest {
    private val testAppModule = module {
        single<Context> { ApplicationProvider.getApplicationContext() }

        // TODO the app does loadModule() here. Should the test do the same?
        scope(koinUiScopeQualifier) {
            scoped<DeviceLocationService> {
                object : DeviceLocationService {
                    override suspend fun isDeviceLocationCapable(): Boolean = fail()
                    override suspend fun getCurrentDeviceLocation(): DeviceLocationResult = fail()
                }
            }
        }
    }

    @Test
    fun checkModules() {
        koinModulesTest(testAppModule)
    }
}
