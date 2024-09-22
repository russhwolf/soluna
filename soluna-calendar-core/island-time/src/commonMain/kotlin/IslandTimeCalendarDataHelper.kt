package com.russhwolf.soluna.calendar

import io.islandtime.Date
import io.islandtime.DateTime
import io.islandtime.DayOfWeek
import io.islandtime.Month
import io.islandtime.TimeZone
import io.islandtime.YearMonth
import io.islandtime.at
import io.islandtime.toInstant

class IslandTimeCalendarDataHelper(val timeZone: TimeZone) : CalendarDataHelper<Month, DayOfWeek> {

    override fun getDaysInMonth(year: Int, month: Month): Int {
        val yearMonth = YearMonth(year, month)
        return yearMonth.lastDay
    }

    override fun getDstEvent(year: Int, month: Month, day: Int): DstEvent? {
        val startOfDay = DateTime(year, month, day, 0, 0).at(timeZone).toInstant()
        val endOfDay = DateTime(year, month, day, 23, 59, 59, 999_999_999).at(timeZone).toInstant()

        return when {
            !timeZone.rules.isDaylightSavingsAt(startOfDay) && timeZone.rules.isDaylightSavingsAt(endOfDay) -> DstEvent.Start
            timeZone.rules.isDaylightSavingsAt(startOfDay) && !timeZone.rules.isDaylightSavingsAt(endOfDay) -> DstEvent.End
            else -> null
        }
    }

    override fun getWeekdayOfMonthStart(year: Int, month: Month): DayOfWeek {
        val firstOfMonth = Date(year, month, 1)
        return firstOfMonth.dayOfWeek
    }

    override fun getIndexOfDayOfWeek(dayOfWeek: DayOfWeek): Int = dayOfWeek.ordinal
}

