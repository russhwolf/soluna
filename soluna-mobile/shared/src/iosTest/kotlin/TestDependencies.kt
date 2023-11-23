package com.russhwolf.soluna.mobile

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.inMemoryDriver
import com.russhwolf.soluna.mobile.db.SolunaDb
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

actual fun <T> runBlocking(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T =
    kotlinx.coroutines.runBlocking(context, block)

actual fun createInMemorySqlDriver(): SqlDriver {
    val schema = SolunaDb.Schema
    return inMemoryDriver(schema)
}

