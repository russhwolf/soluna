package com.russhwolf.soluna.calendar

import com.russhwolf.soluna.MoonPhase
import com.russhwolf.soluna.RiseSetResult

data class CalendarMonthData<TimeUnit : Any, Month : Any, DayOfWeek : Any, TimeZone : Any>(
    val year: Int,
    val month: Month,
    private val week0: CalendarWeekData<TimeUnit>,
    private val week1: CalendarWeekData<TimeUnit>,
    private val week2: CalendarWeekData<TimeUnit>,
    private val week3: CalendarWeekData<TimeUnit>,
    private val week4: CalendarWeekData<TimeUnit>?,
    private val week5: CalendarWeekData<TimeUnit>?,
    val firstDayOfWeek: DayOfWeek,
    val latitude: Double,
    val longitude: Double,
    val timeZone: TimeZone
) {
    val weeks: List<CalendarWeekData<TimeUnit>> = listOfNotNull(week0, week1, week2, week3, week4, week5)

    operator fun get(index: Int) = weeks[index]
}

data class CalendarWeekData<TimeUnit : Any>(
    private val day0: CalendarCellData<TimeUnit>,
    private val day1: CalendarCellData<TimeUnit>,
    private val day2: CalendarCellData<TimeUnit>,
    private val day3: CalendarCellData<TimeUnit>,
    private val day4: CalendarCellData<TimeUnit>,
    private val day5: CalendarCellData<TimeUnit>,
    private val day6: CalendarCellData<TimeUnit>
) {
    val days: List<CalendarCellData<TimeUnit>> = listOf(day0, day1, day2, day3, day4, day5, day6)

    operator fun get(index: Int) = days[index]
}


sealed interface CalendarCellData<out TimeUnit : Any> {
    data object Empty : CalendarCellData<Nothing>
    data class Data<TimeUnit : Any>(
        val date: Int,
        val sunTimes: RiseSetResult<TimeUnit>,
        val moonTimes: RiseSetResult<TimeUnit>,
        val moonPhase: MoonPhase?,
        val dst: DstEvent?
    ) : CalendarCellData<TimeUnit>
}

enum class DstEvent {
    Start, End
}

