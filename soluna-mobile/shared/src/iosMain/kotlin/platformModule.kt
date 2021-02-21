package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import io.ktor.client.engine.ios.Ios
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single<SqlDriver> { NativeSqliteDriver(SolunaDb.Schema, "Soluna.db") }
    single { Ios.create() }
    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
    single(dbDispatcherQualifier) { Dispatchers.Default }
}
