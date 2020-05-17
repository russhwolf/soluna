package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> { AndroidSqliteDriver(SolunaDb.Schema, get()) }
    single { Android.create() }
    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
}
