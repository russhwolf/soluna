package com.russhwolf.soluna.mobile

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.russhwolf.soluna.mobile.repository.AndroidDeviceLocationService
import com.russhwolf.soluna.mobile.repository.DeviceLocationService
import com.russhwolf.soluna.mobile.repository.LocationPermissionRequester
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.check.checkModules
import kotlin.test.AfterTest
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class AndroidKoinModulesTest {
    private val applicationContext: Context = ApplicationProvider.getApplicationContext()

    private val testAppModule = module {
        single { applicationContext }

        scope(koinUiScopeQualifier) {
            scoped<DeviceLocationService> {
                AndroidDeviceLocationService(applicationContext, get())
            }
        }
    }

    @Test
    fun checkModules() {
        val koinApplication = initKoin(testAppModule)

        koinApplication.koin.loadModules(listOf(module {
            scope(koinUiScopeQualifier) {
                scoped<LocationPermissionRequester> {
                    // Fake permission requester to fulfill the dependency
                    object : LocationPermissionRequester {
                        override suspend fun requestLocationPermission(): Boolean = fail()
                    }
                }
            }
        }))

        koinApplication.checkModules {
            create<LocationDetailViewModel> { parametersOf(1L) }
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }
}
