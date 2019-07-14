package com.russhwolf.soluna.mobile

import co.touchlab.sqliter.DatabaseConfiguration
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import com.squareup.sqldelight.drivers.ios.wrapConnection
import kotlin.reflect.KClass

actual fun createInMemorySqlDriver(): SqlDriver {
    val schema = SolunaDb.Schema
    return NativeSqliteDriver(
        DatabaseConfiguration(
            name = "SolunaDbTest.db",
            version = schema.version,
            create = { connection ->
                wrapConnection(connection) { schema.create(it) }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { schema.migrate(it, oldVersion, newVersion) }
            },
            inMemory = true
        )
    )
}

actual annotation class RunWith(actual val value: KClass<out Runner>)
actual abstract class Runner
actual class AndroidJUnit4 : Runner()
