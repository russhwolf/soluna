package com.russhwolf.soluna.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.russhwolf.soluna.time.LocalTimeAstronomicalCalculator
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview
import soluna.soluna_compose_calendar.generated.resources.Res
import soluna.soluna_compose_calendar.generated.resources.Symbola


@Composable
fun CalendarPage(calendarMonthContent: CalendarMonthContent) {
    Surface(Modifier.padding(16.dp), color = Color.White) {
        Column {
            Text(calendarMonthContent.title, Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Column {
                Row {
                    calendarMonthContent.daysOfWeek.forEach {
                        Text(it, Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }
                Column(Modifier.border(3.dp, Color.Black)) {
                    calendarMonthContent.weeks.forEach { weekContent ->
                        Row(Modifier.height(IntrinsicSize.Min)) {
                            weekContent.days.forEach { cellContent ->
                                Box(Modifier.border(1.dp, Color.Black).padding(8.dp).weight(1f)) {
                                    when (cellContent) {
                                        is CalendarCellContent.Empty -> {
                                            Box(Modifier.fillMaxHeight()) {}
                                        }

                                        is CalendarCellContent.Content -> {
                                            Column {
                                                Row {
                                                    Text(cellContent.date)
                                                    Box(Modifier.weight(1f))
                                                    Text(cellContent.dst, textAlign = TextAlign.End)
                                                    Text(
                                                        cellContent.moonPhase,
                                                        textAlign = TextAlign.End,
                                                        fontFamily = FontFamily(Font(Res.font.Symbola))
                                                    )
                                                }
                                                Text(cellContent.sunText)
                                                Text(cellContent.moonText)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Row {
                Text(calendarMonthContent.locationText)
                Box(Modifier.weight(1f))
                Text(calendarMonthContent.aboutText)
            }
        }
    }
}

@Preview
@Composable
fun CalendarPage_Preview_Somerville() {
    val year = 2019
    val month = Month.MARCH
    val firstDayOfWeek = DayOfWeek.SUNDAY
    val timeZone = TimeZone.of("America/New_York")
    val latitude = 42.3871772
    val longitude = -71.1007049
    val calendarDataGenerator = CalendarDataGenerator(
        KotlinxDateTimeCalendarDataHelper(timeZone),
        LocalTimeAstronomicalCalculator.factory(timeZone, latitude, longitude)
    )
    val calendarMonthData =
        calendarDataGenerator.generateCalendarMonth(year, month, firstDayOfWeek, latitude, longitude, timeZone)
    val calendarMonthContent = calendarMonthData.toCalendarMonthContent()

    CalendarPage(calendarMonthContent)
}

@Preview
@Composable
fun CalendarPage_Preview_Troll() {
    val year = 2023
    val month = Month.OCTOBER
    val firstDayOfWeek = DayOfWeek.SUNDAY
    val timeZone = TimeZone.of("Antarctica/Troll")
    val latitude = -72.0121236
    val longitude = 2.5240873
    val calendarDataGenerator = CalendarDataGenerator(
        KotlinxDateTimeCalendarDataHelper(timeZone),
        LocalTimeAstronomicalCalculator.factory(timeZone, latitude, longitude)
    )
    val calendarMonthData =
        calendarDataGenerator.generateCalendarMonth(year, month, firstDayOfWeek, latitude, longitude, timeZone)
    val calendarMonthContent = calendarMonthData.toCalendarMonthContent()

    CalendarPage(calendarMonthContent)
}
