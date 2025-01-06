package com.russhwolf.soluna.mobile.db

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.russhwolf.soluna.mobile.db.sqldelight.Reminder
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb

operator fun SolunaDb.Companion.invoke(driver: SqlDriver) =
    SolunaDb(driver, Reminder.Adapter(EnumColumnAdapter(), LongAsIntAdapter))

enum class ReminderType {
    Sunrise, Sunset, Moonrise, Moonset
}

private object LongAsIntAdapter : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}

