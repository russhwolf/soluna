package com.russhwolf.soluna.mobile

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.repository.DeviceLocationService
import com.russhwolf.soluna.mobile.repository.IosDeviceLocationService
import com.russhwolf.soluna.mobile.screen.BasePlatformViewModel
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

internal actual val platformModule: Module = module {
    single<SqlDriver> { NativeSqliteDriver(SolunaDb.Schema, "Soluna.db") }
    single { Darwin.create() }
    single {
        val userDefaults = NSUserDefaults(suiteName = "soluna_settings")
        val dispatcher = get<CoroutineDispatcher>(ioDispatcherQualifier)
        NSUserDefaultsSettings(userDefaults).toFlowSettings(dispatcher)
    }
    single { Logger.DEFAULT }
    single<DeviceLocationService> { IosDeviceLocationService() }
    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
    single(ioDispatcherQualifier) { Dispatchers.Default }
}

actual inline fun <reified T : BasePlatformViewModel> ScopeDSL.viewModel(
    qualifier: Qualifier?,
    noinline definition: Definition<T>
): KoinDefinition<T> {
    return factory(qualifier, definition)
}
