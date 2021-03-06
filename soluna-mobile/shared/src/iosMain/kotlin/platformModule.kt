package com.russhwolf.soluna.mobile

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.repository.DeviceLocationService
import com.russhwolf.soluna.mobile.repository.IosDeviceLocationService
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import io.ktor.client.engine.ios.Ios
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

internal actual val platformModule: Module = module {
    single<SqlDriver> { NativeSqliteDriver(SolunaDb.Schema, "Soluna.db") }
    single { Ios.create() }
    single {
        val userDefaults = NSUserDefaults(suiteName = "soluna_settings")
        val dispatcher = get<CoroutineDispatcher>(ioDispatcherQualifier)
        AppleSettings(userDefaults, useFrozenListeners = true).toFlowSettings(dispatcher)
    }
    single { Logger.DEFAULT }
    single<DeviceLocationService> { IosDeviceLocationService() }
    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
    single(ioDispatcherQualifier) { Dispatchers.Default }
}
