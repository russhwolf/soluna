package com.russhwolf.soluna.mobile.db

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver

fun createDatabase(driver: SqlDriver) = SolunaDb(driver, Reminder.Adapter(EnumColumnAdapter(), LongAsIntAdapter))

private object LongAsIntAdapter : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()

    override fun encode(value: Int): Long = value.toLong()

}
