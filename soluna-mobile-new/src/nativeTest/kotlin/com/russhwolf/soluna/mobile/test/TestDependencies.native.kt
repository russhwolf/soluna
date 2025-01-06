package com.russhwolf.soluna.mobile.test

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.inMemoryDriver
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb

actual fun createInMemorySqlDriver(): SqlDriver = inMemoryDriver(SolunaDb.Schema)
