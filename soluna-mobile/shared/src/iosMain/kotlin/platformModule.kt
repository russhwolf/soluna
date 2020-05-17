package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.util.NsQueueDispatcher
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import io.ktor.client.engine.ios.Ios
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.darwin.dispatch_get_main_queue

internal actual val platformModule: Module = module {
    single<SqlDriver> { NativeSqliteDriver(SolunaDb.Schema, "Soluna.db") }
    single { Ios.create() }
    single<CoroutineDispatcher>(mainDispatcherQualifier) { NsQueueDispatcher(dispatch_get_main_queue()) }
}
