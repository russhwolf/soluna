package com.russhwolf.soluna.mobile

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineScope
import org.robolectric.Shadows
import kotlin.coroutines.CoroutineContext

actual fun <T> runBlocking(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T =
    kotlinx.coroutines.runBlocking(context, block)

actual fun createInMemorySqlDriver(): SqlDriver =
    AndroidSqliteDriver(SolunaDb.Schema, ApplicationProvider.getApplicationContext())

actual typealias RunWith = org.junit.runner.RunWith
actual typealias Runner = org.junit.runner.Runner
actual typealias AndroidJUnit4 = androidx.test.ext.junit.runners.AndroidJUnit4

actual fun blockUntilIdle() = Shadows.shadowOf(Looper.getMainLooper()).idle()
