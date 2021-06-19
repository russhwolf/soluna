package com.russhwolf.soluna.mobile

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.android.Android
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.MessageLengthLimitingLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val platformModule: Module = module {
    single<SqlDriver> { AndroidSqliteDriver(SolunaDb.Schema, get(), "Soluna.db") }
    single { Android.create() }
    single<FlowSettings> {
        val context: Context = get()
        val ioDispatcher: CoroutineDispatcher = get(ioDispatcherQualifier)
        val dataStore = PreferenceDataStoreFactory.create(scope = CoroutineScope(ioDispatcher)) {
            File(context.filesDir, "soluna_settings.preferences_pb")
        }
        DataStoreSettings(dataStore)
    }
    single<Logger> {
        val tag = "HttpClient"
        val logger = object : Logger {
            override fun log(message: String) {
                Log.i(tag, message)
            }
        }
        MessageLengthLimitingLogger(delegate = logger)
    }
    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
    single(ioDispatcherQualifier) { Dispatchers.IO }
}
