package com.russhwolf.soluna.calendar

import com.russhwolf.soluna.AstronomicalCalculator

class CalendarDataGenerator<TimeUnit : Any, Month : Any, DayOfWeek : Any>(
    private val calendarDataHelper: CalendarDataHelper<Month, DayOfWeek>,
    private val calculatorFactory: (Int, Month, Int) -> AstronomicalCalculator<TimeUnit>
) {
    fun <TimeZone : Any> generateCalendarMonth(
        year: Int,
        month: Month,
        firstDayOfWeek: DayOfWeek,
        latitude: Double,
        longitude: Double,
        timeZone: TimeZone
    ): CalendarMonthData<TimeUnit, Month, DayOfWeek, TimeZone> {
        val weekdayOfMonthStart = calendarDataHelper.getWeekdayOfMonthStart(year, month)
        val daysInMonth = calendarDataHelper.getDaysInMonth(year, month)
        // Using .mod() instead of % to force positive sign
        val emptyDaysBefore =
            (calendarDataHelper.getIndexOfDayOfWeek(weekdayOfMonthStart) - calendarDataHelper.getIndexOfDayOfWeek(
                firstDayOfWeek
            ))
                .mod(7)
        val emptyDaysAfter = (7 - (emptyDaysBefore + daysInMonth)).mod(7)

        val cells = buildList {
            repeat(emptyDaysBefore) {
                add(CalendarCellData.Empty)
            }

            for (day in 1..daysInMonth) {
                val calculator = calculatorFactory(year, month, day)
                add(
                    CalendarCellData.Data(
                        day,
                        calculator.sunTimes,
                        calculator.moonTimes,
                        calculator.moonPhase,
                        calendarDataHelper.getDstEvent(year, month, day)
                    )
                )
            }

            repeat(emptyDaysAfter) {
                add(CalendarCellData.Empty)
            }
        }

        val weeks = cells.chunked(7).map {
            CalendarWeekData(it[0], it[1], it[2], it[3], it[4], it[5], it[6])
        }

        return CalendarMonthData(
            year = year,
            month = month,
            week0 = weeks[0],
            week1 = weeks[1],
            week2 = weeks[2],
            week3 = weeks[3],
            week4 = weeks.getOrNull(4),
            week5 = weeks.getOrNull(5),
            firstDayOfWeek = firstDayOfWeek,
            latitude = latitude,
            longitude = longitude,
            timeZone = timeZone
        )
    }

}

interface CalendarDataHelper<Month : Any, DayOfWeek : Any> {
    fun getDaysInMonth(year: Int, month: Month): Int
    fun getDstEvent(year: Int, month: Month, day: Int): DstEvent?
    fun getWeekdayOfMonthStart(year: Int, month: Month): DayOfWeek
    fun getIndexOfDayOfWeek(dayOfWeek: DayOfWeek): Int
}
