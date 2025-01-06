package com.russhwolf.soluna.mobile.test

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb

actual fun createInMemorySqlDriver(): SqlDriver =
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { SolunaDb.Schema.create(it) }
