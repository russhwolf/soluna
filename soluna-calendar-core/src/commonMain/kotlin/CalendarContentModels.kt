package com.russhwolf.soluna.calendar

data class CalendarMonthContent(
    val title: String,
    private val week0: CalendarWeekContent,
    private val week1: CalendarWeekContent,
    private val week2: CalendarWeekContent,
    private val week3: CalendarWeekContent,
    private val week4: CalendarWeekContent?,
    private val week5: CalendarWeekContent?,
    private val dayOfWeek0: String,
    private val dayOfWeek1: String,
    private val dayOfWeek2: String,
    private val dayOfWeek3: String,
    private val dayOfWeek4: String,
    private val dayOfWeek5: String,
    private val dayOfWeek6: String,
    val locationText: String,
    val aboutText: String
) {
    val weeks: List<CalendarWeekContent> = listOfNotNull(week0, week1, week2, week3, week4, week5)

    val daysOfWeek: List<String> =
        listOf(dayOfWeek0, dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, dayOfWeek6)

    operator fun get(index: Int): CalendarWeekContent = weeks[index]
}

data class CalendarWeekContent(
    private val day0: CalendarCellContent,
    private val day1: CalendarCellContent,
    private val day2: CalendarCellContent,
    private val day3: CalendarCellContent,
    private val day4: CalendarCellContent,
    private val day5: CalendarCellContent,
    private val day6: CalendarCellContent
) {
    val days: List<CalendarCellContent> = listOf(day0, day1, day2, day3, day4, day5, day6)

    operator fun get(index: Int): CalendarCellContent = days[index]
}

sealed interface CalendarCellContent {
    data object Empty : CalendarCellContent
    data class Content(
        val date: String,
        val sunText: String,
        val moonText: String,
        val moonPhase: String = "",
        val dst: String = ""
    ) : CalendarCellContent
}

