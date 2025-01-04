package com.russhwolf.soluna.calendar

import com.russhwolf.soluna.MoonPhase
import com.russhwolf.soluna.RiseSetResult
import io.islandtime.DayOfWeek
import io.islandtime.Month
import io.islandtime.Time
import io.islandtime.TimeZone
import io.islandtime.YearMonth
import io.islandtime.jvm.toJavaLocalTime
import io.islandtime.jvm.toJavaYearMonth
import io.islandtime.measures.days
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.abs

fun CalendarMonthData<Time, Month, DayOfWeek, TimeZone>.toCalendarMonthContent(): CalendarMonthContent {
    val yearMonthFormatter = DateTimeFormatter.ofPattern("MMMM, yyyy")
    val dayOfWeekFormatter = DateTimeFormatter.ofPattern("eeee")

    return CalendarMonthContent(
        title = YearMonth(year, month).toJavaYearMonth().format(yearMonthFormatter),
        week0 = weeks[0].toCalendarWeekContent(),
        week1 = weeks[1].toCalendarWeekContent(),
        week2 = weeks[2].toCalendarWeekContent(),
        week3 = weeks[3].toCalendarWeekContent(),
        week4 = weeks.getOrNull(4)?.toCalendarWeekContent(),
        week5 = weeks.getOrNull(5)?.toCalendarWeekContent(),
        dayOfWeek0 = dayOfWeekFormatter.format(firstDayOfWeek.toJavaDayOfWeek()),
        dayOfWeek1 = dayOfWeekFormatter.format((firstDayOfWeek + 1.days).toJavaDayOfWeek()),
        dayOfWeek2 = dayOfWeekFormatter.format((firstDayOfWeek + 2.days).toJavaDayOfWeek()),
        dayOfWeek3 = dayOfWeekFormatter.format((firstDayOfWeek + 3.days).toJavaDayOfWeek()),
        dayOfWeek4 = dayOfWeekFormatter.format((firstDayOfWeek + 4.days).toJavaDayOfWeek()),
        dayOfWeek5 = dayOfWeekFormatter.format((firstDayOfWeek + 5.days).toJavaDayOfWeek()),
        dayOfWeek6 = dayOfWeekFormatter.format((firstDayOfWeek + 6.days).toJavaDayOfWeek()),
        locationText = "Times computed for %s, %s using timezone %s".format(
            latitude.latitudeString(),
            longitude.longitudeString(),
            timeZone.id
        ),
        aboutText = "Copyright © 2025 Russell Wolf. All rights reserved."
    )
}

// This helper doesn't exist in Island Time (yet?)
private fun DayOfWeek.toJavaDayOfWeek(): java.time.DayOfWeek = java.time.DayOfWeek.entries[ordinal]

fun CalendarWeekData<Time>.toCalendarWeekContent() = CalendarWeekContent(
    day0 = days[0].toCalendarCellContent(),
    day1 = days[1].toCalendarCellContent(),
    day2 = days[2].toCalendarCellContent(),
    day3 = days[3].toCalendarCellContent(),
    day4 = days[4].toCalendarCellContent(),
    day5 = days[5].toCalendarCellContent(),
    day6 = days[6].toCalendarCellContent()
)

fun CalendarCellData<Time>.toCalendarCellContent(): CalendarCellContent = when (this) {
    CalendarCellData.Empty -> CalendarCellContent.Empty
    is CalendarCellData.Data -> CalendarCellContent.Content(
        date = date.toString(),
        sunText = sunTimes.toText("Sun"),
        moonText = moonTimes.toText("Moon"),
        moonPhase = when (moonPhase) {
            MoonPhase.NEW -> "\uD83C\uDF11"
            MoonPhase.FIRST_QUARTER -> "\uD83C\uDF13"
            MoonPhase.FULL -> "\uD83C\uDF15"
            MoonPhase.LAST_QUARTER -> "\uD83C\uDF17"
            null -> ""
        },
        dst = when (dst) {
            DstEvent.Start -> "DST starts"
            DstEvent.End -> "DST ends"
            else -> ""
        } + if (moonPhase != null) "\u2002\u2003" else "", // offset so dst doesn't draw over moon phase
    )
}

private fun RiseSetResult<Time>.toText(prefix: String): String = when (this) {
    is RiseSetResult.RiseThenSet -> "${prefix}rise: ${riseTime.formatTime()}\n${prefix}set: ${setTime.formatTime()}"
    is RiseSetResult.SetThenRise -> "${prefix}set: ${setTime.formatTime()}\n${prefix}rise: ${riseTime.formatTime()}"
    is RiseSetResult.RiseOnly -> "${prefix}rise: ${riseTime.formatTime()}\nNo ${prefix}set"
    is RiseSetResult.SetOnly -> "${prefix}set: ${setTime.formatTime()}\nNo ${prefix}rise"
    RiseSetResult.UpAllDay -> "$prefix up all day"
    RiseSetResult.DownAllDay -> "$prefix down all day"
    RiseSetResult.Unknown -> "No $prefix events"
}

private fun Time.formatTime() = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(toJavaLocalTime())

private fun Double.latitudeString(): String = positionString(if (this < 0) "S" else "N")
private fun Double.longitudeString(): String = positionString(if (this < 0) "W" else "E")
private fun Double.positionString(direction: String): String = "%1.3f°$direction".format(abs(this))
