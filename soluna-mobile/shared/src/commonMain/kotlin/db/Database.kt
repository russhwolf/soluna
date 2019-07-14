package com.russhwolf.soluna.mobile.db

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver

fun createDatabase(driver: SqlDriver) = SolunaDb(driver, Reminder.Adapter(EnumColumnAdapter()))

