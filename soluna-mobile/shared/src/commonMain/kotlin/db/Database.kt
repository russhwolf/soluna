package com.russhwolf.soluna.mobile.db

import com.squareup.sqldelight.db.SqlDriver

fun createDatabase(driver: SqlDriver) = SolunaDb(driver)

