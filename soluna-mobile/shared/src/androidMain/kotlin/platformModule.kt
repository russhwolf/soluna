package com.russhwolf.soluna.mobile

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val platformModule: Module = module {
    single<SqlDriver> { AndroidSqliteDriver(SolunaDb.Schema, get()) }
    single { Android.create() }
    single<FlowSettings> {
        val context: Context = get()
        val ioDispatcher: CoroutineDispatcher = get(ioDispatcherQualifier)
        val dataStore = PreferenceDataStoreFactory.create(scope = CoroutineScope(ioDispatcher)) {
            File(context.filesDir, "soluna_settings.preferences_pb")
        }
        DataStoreSettings(dataStore)
    }
    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
    single(ioDispatcherQualifier) { Dispatchers.IO }
}
