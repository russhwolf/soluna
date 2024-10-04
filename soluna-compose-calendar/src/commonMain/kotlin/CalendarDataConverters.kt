package com.russhwolf.soluna.calendar

import androidx.compose.runtime.Composable
import com.russhwolf.soluna.MoonPhase
import com.russhwolf.soluna.RiseSetResult
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource
import soluna.soluna_compose_calendar.generated.resources.Res
import soluna.soluna_compose_calendar.generated.resources.direction_east
import soluna.soluna_compose_calendar.generated.resources.direction_north
import soluna.soluna_compose_calendar.generated.resources.direction_south
import soluna.soluna_compose_calendar.generated.resources.direction_west
import soluna.soluna_compose_calendar.generated.resources.dst_start
import soluna.soluna_compose_calendar.generated.resources.event_down_always
import soluna.soluna_compose_calendar.generated.resources.event_no_rise
import soluna.soluna_compose_calendar.generated.resources.event_no_set
import soluna.soluna_compose_calendar.generated.resources.event_rise
import soluna.soluna_compose_calendar.generated.resources.event_set
import soluna.soluna_compose_calendar.generated.resources.event_unknown
import soluna.soluna_compose_calendar.generated.resources.event_up_always
import soluna.soluna_compose_calendar.generated.resources.footer_about
import soluna.soluna_compose_calendar.generated.resources.footer_location
import soluna.soluna_compose_calendar.generated.resources.moon_phase_first_quarter
import soluna.soluna_compose_calendar.generated.resources.moon_phase_full
import soluna.soluna_compose_calendar.generated.resources.moon_phase_last_quarter
import soluna.soluna_compose_calendar.generated.resources.moon_phase_new
import soluna.soluna_compose_calendar.generated.resources.object_moon
import soluna.soluna_compose_calendar.generated.resources.object_sun
import kotlin.math.abs

@Composable
fun CalendarMonthData<LocalTime, Month, DayOfWeek, TimeZone>.toCalendarMonthContent(): CalendarMonthContent {
    val yearMonthFormatter = LocalDate.Format {
        monthName(MonthNames.ENGLISH_FULL)
        chars(", ")
        year()
    }

    return CalendarMonthContent(
        title = LocalDate(year, month, 1).format(yearMonthFormatter),
        week0 = weeks[0].toCalendarWeekContent(),
        week1 = weeks[1].toCalendarWeekContent(),
        week2 = weeks[2].toCalendarWeekContent(),
        week3 = weeks[3].toCalendarWeekContent(),
        week4 = weeks.getOrNull(4)?.toCalendarWeekContent(),
        week5 = weeks.getOrNull(5)?.toCalendarWeekContent(),
        dayOfWeek0 = DayOfWeekNames.ENGLISH_FULL.names[firstDayOfWeek.ordinal],
        dayOfWeek1 = DayOfWeekNames.ENGLISH_FULL.names[(firstDayOfWeek.ordinal + 1) % 7],
        dayOfWeek2 = DayOfWeekNames.ENGLISH_FULL.names[(firstDayOfWeek.ordinal + 2) % 7],
        dayOfWeek3 = DayOfWeekNames.ENGLISH_FULL.names[(firstDayOfWeek.ordinal + 3) % 7],
        dayOfWeek4 = DayOfWeekNames.ENGLISH_FULL.names[(firstDayOfWeek.ordinal + 4) % 7],
        dayOfWeek5 = DayOfWeekNames.ENGLISH_FULL.names[(firstDayOfWeek.ordinal + 5) % 7],
        dayOfWeek6 = DayOfWeekNames.ENGLISH_FULL.names[(firstDayOfWeek.ordinal + 6) % 7],
        locationText = stringResource(
            Res.string.footer_location,
            latitude.latitudeString(),
            longitude.longitudeString(),
            timeZone.id
        ),
        aboutText = stringResource(Res.string.footer_about)
    )
}

@Composable
fun CalendarWeekData<LocalTime>.toCalendarWeekContent() = CalendarWeekContent(
    day0 = days[0].toCalendarCellContent(),
    day1 = days[1].toCalendarCellContent(),
    day2 = days[2].toCalendarCellContent(),
    day3 = days[3].toCalendarCellContent(),
    day4 = days[4].toCalendarCellContent(),
    day5 = days[5].toCalendarCellContent(),
    day6 = days[6].toCalendarCellContent()
)

@Composable
fun CalendarCellData<LocalTime>.toCalendarCellContent(): CalendarCellContent = when (this) {
    CalendarCellData.Empty -> CalendarCellContent.Empty
    is CalendarCellData.Data -> CalendarCellContent.Content(
        date = date.toString(),
        sunText = sunTimes.toText(stringResource(Res.string.object_sun)),
        moonText = moonTimes.toText(stringResource(Res.string.object_moon)),
        moonPhase = when (moonPhase) {
            MoonPhase.NEW -> stringResource(Res.string.moon_phase_new)
            MoonPhase.FIRST_QUARTER -> stringResource(Res.string.moon_phase_first_quarter)
            MoonPhase.FULL -> stringResource(Res.string.moon_phase_full)
            MoonPhase.LAST_QUARTER -> stringResource(Res.string.moon_phase_last_quarter)
            null -> ""
        },
        dst = when (dst) {
            DstEvent.Start -> stringResource(Res.string.dst_start)
            DstEvent.End -> stringResource(Res.string.dst_start)
            else -> ""
        }
    )
}

@Composable
private fun RiseSetResult<LocalTime>.toText(prefix: String): String = when (this) {
    is RiseSetResult.RiseThenSet -> "${
        stringResource(
            Res.string.event_rise,
            prefix,
            riseTime.formatTime()
        )
    }\n${stringResource(Res.string.event_set, prefix, setTime.formatTime())}"

    is RiseSetResult.SetThenRise -> "${
        stringResource(
            Res.string.event_set,
            prefix,
            setTime.formatTime()
        )
    }\n${stringResource(Res.string.event_rise, prefix, riseTime.formatTime())}"

    is RiseSetResult.RiseOnly -> "${
        stringResource(
            Res.string.event_rise,
            prefix,
            riseTime.formatTime()
        )
    }\n${stringResource(Res.string.event_no_set, prefix)}"

    is RiseSetResult.SetOnly -> "${
        stringResource(
            Res.string.event_set,
            prefix,
            setTime.formatTime()
        )
    }\n${stringResource(Res.string.event_no_rise, prefix)}"

    RiseSetResult.UpAllDay -> stringResource(Res.string.event_up_always, prefix) + '\n'
    RiseSetResult.DownAllDay -> stringResource(Res.string.event_down_always, prefix) + '\n'
    RiseSetResult.Unknown -> stringResource(Res.string.event_unknown, prefix) + '\n'
}

val timeFormat = LocalTime.Format {
    amPmHour(padding = Padding.NONE)
    char(':')
    minute(padding = Padding.ZERO)
    char(' ')
    amPmMarker("AM", "PM")
}

private fun LocalTime.formatTime() = timeFormat.format(this)

@Composable
private fun Double.latitudeString(): String =
    positionString(stringResource(if (this < 0) Res.string.direction_south else Res.string.direction_north))

@Composable
private fun Double.longitudeString(): String =
    positionString(stringResource(if (this < 0) Res.string.direction_west else Res.string.direction_east))

@Composable
private fun Double.positionString(direction: String): String = "${(abs(this) * 1000).toInt() / 1000.0}°$direction"
