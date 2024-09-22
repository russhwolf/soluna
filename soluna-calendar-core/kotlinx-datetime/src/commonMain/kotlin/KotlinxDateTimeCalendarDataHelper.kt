package com.russhwolf.soluna.calendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.offsetAt
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

class KotlinxDateTimeCalendarDataHelper(val timeZone: TimeZone) : CalendarDataHelper<Month, DayOfWeek> {
    override fun getDaysInMonth(year: Int, month: Month): Int {
        val startOfMonth = LocalDate(year, month, 1)
        val endOfMonth = startOfMonth.plus(1, DateTimeUnit.MONTH)
        return startOfMonth.daysUntil(endOfMonth)
    }

    override fun getDstEvent(year: Int, month: Month, day: Int): DstEvent? {
        val date = LocalDate(year, month, day)
        val startOfDay = date.atTime(0, 0).toInstant(timeZone)
        val endOfDay = date.atTime(23, 59, 59, 999_999_999).toInstant(timeZone)

        val startOffset = timeZone.offsetAt(startOfDay).totalSeconds
        val endOffset = timeZone.offsetAt(endOfDay).totalSeconds

        return when {
            startOffset < endOffset -> DstEvent.Start
            startOffset > endOffset -> DstEvent.End
            else -> null
        }
    }

    override fun getWeekdayOfMonthStart(year: Int, month: Month): DayOfWeek {
        val firstOfMonth = LocalDate(year, month, 1)
        return firstOfMonth.dayOfWeek
    }

    override fun getIndexOfDayOfWeek(dayOfWeek: DayOfWeek): Int = dayOfWeek.isoDayNumber
}
