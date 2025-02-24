package com.russhwolf.soluna.mobile.db

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.russhwolf.soluna.mobile.db.sqldelight.Location
import com.russhwolf.soluna.mobile.db.sqldelight.Reminder
import com.russhwolf.soluna.mobile.db.sqldelight.SolunaDb
import kotlinx.datetime.TimeZone

operator fun SolunaDb.Companion.invoke(driver: SqlDriver) =
    SolunaDb(driver, Location.Adapter(TimeZoneAdapter), Reminder.Adapter(EnumColumnAdapter(), LongAsIntAdapter))

private object TimeZoneAdapter : ColumnAdapter<TimeZone, String> {
    override fun decode(databaseValue: String): TimeZone = TimeZone.of(databaseValue)
    override fun encode(value: TimeZone): String = value.id
}

enum class ReminderType {
    Sunrise, Sunset, Moonrise, Moonset
}

private object LongAsIntAdapter : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}

