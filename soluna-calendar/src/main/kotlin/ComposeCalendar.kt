@file:OptIn(ExperimentalFoundationApi::class)

package com.russhwolf.soluna.calendar

import androidx.compose.desktop.ComposePanel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.text.platform.font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.russhwolf.soluna.MoonPhase
import com.russhwolf.soluna.calendar.CalendarTheme.Dimensions.marginColumnHeader
import com.russhwolf.soluna.calendar.CalendarTheme.Dimensions.marginFooter
import com.russhwolf.soluna.calendar.CalendarTheme.Dimensions.marginHeader
import com.russhwolf.soluna.calendar.CalendarTheme.Dimensions.marginHorizontal
import com.russhwolf.soluna.calendar.CalendarTheme.Dimensions.marginInternal
import com.russhwolf.soluna.calendar.CalendarTheme.Dimensions.marginTitle
import com.russhwolf.soluna.calendar.CalendarTheme.Dimensions.strokeWidthInner
import com.russhwolf.soluna.calendar.CalendarTheme.Dimensions.strokeWidthOuter
import com.russhwolf.soluna.calendar.CalendarTheme.PAGE_HEIGHT
import com.russhwolf.soluna.calendar.CalendarTheme.PAGE_WIDTH
import com.russhwolf.soluna.calendar.CalendarTheme.Typography.dateStyle
import com.russhwolf.soluna.calendar.CalendarTheme.Typography.dstStyle
import com.russhwolf.soluna.calendar.CalendarTheme.Typography.footerStyle
import com.russhwolf.soluna.calendar.CalendarTheme.Typography.symbolStyle
import com.russhwolf.soluna.calendar.CalendarTheme.Typography.timeStyle
import com.russhwolf.soluna.calendar.CalendarTheme.Typography.titleStyle
import com.russhwolf.soluna.calendar.CalendarTheme.Typography.weekdayStyle
import com.russhwolf.soluna.time.moonPhase
import com.russhwolf.soluna.time.moonTimes
import com.russhwolf.soluna.time.sunTimes
import io.islandtime.Date
import io.islandtime.DayOfWeek
import io.islandtime.Month
import io.islandtime.TimeZone
import io.islandtime.YearMonth
import io.islandtime.ZonedDateTime
import io.islandtime.at
import io.islandtime.atTime
import io.islandtime.calendar.WeekSettings
import io.islandtime.jvm.toJavaYearMonth
import io.islandtime.jvm.toJavaZonedDateTime
import io.islandtime.measures.IntDays
import io.islandtime.measures.milliseconds
import io.islandtime.weekOfMonth
import org.jetbrains.skiko.SkiaLayer
import java.awt.BorderLayout
import java.awt.Component
import java.awt.image.BufferedImage
import java.io.File
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.WindowConstants
import kotlin.math.abs
import kotlin.math.roundToInt

internal fun renderComposeCalendarToFile(
    locationName: String,
    month: Month,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone
): File {
    val panel = ComposePanel()

    val window = JFrame()
    window.contentPane.add(panel, BorderLayout.CENTER)
    window.setSize(PAGE_WIDTH, PAGE_HEIGHT)
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.isUndecorated = true
    window.isVisible = true

    panel.renderCalendar(month, year, latitude, longitude, timeZone)
    panel.requestFocus()
    // Wait for panel to request focus and then pass it to its internal SkiaPanel
    var focus: Component? = null
    while (focus !is SkiaLayer) {
        focus = window.focusOwner
        Thread.sleep(100)
    }

    val skiaLayer = window.focusOwner as SkiaLayer
    // TODO there's a factor of 2 somewhere messing things up
    val bitmap = ImageBitmap(2 * window.width, 2 * window.height)
    val canvas = Canvas(bitmap)
    skiaLayer.renderer?.onRender(canvas.nativeCanvas, bitmap.width, bitmap.height)

    val file = File("$locationName-$year-${month.number}.png")
    val image = BufferedImage(bitmap.width, bitmap.height, BufferedImage.TYPE_INT_RGB)
    val pixelMap = bitmap.toPixelMap()
    for (x in 0 until bitmap.width) {
        for (y in 0 until bitmap.height) {
            image.setRGB(x, y, pixelMap[x, y].toArgb())
        }
    }
    ImageIO.write(image, "png", file)

    window.dispose()

    return file
}

fun ComposePanel.renderCalendar(
    month: Month,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone
) {
    setContent {
        Calendar(month, year, latitude, longitude, timeZone)
    }
}

@Composable
private fun Calendar(
    month: Month,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone
) {
    val weekSettings = WeekSettings(DayOfWeek.SUNDAY, 1)
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(
                start = marginHorizontal - strokeWidthOuter / 2,
                end = marginHorizontal - strokeWidthOuter / 2,
                // TODO top margin is a little bit off still
                top = marginHeader - titleStyle.fontSize.value.dp - weekdayStyle.fontSize.value.dp - marginTitle - marginColumnHeader,
                bottom = marginFooter - footerStyle.fontSize.value.dp
            )
        ) {
            CalendarHeader(month, year, weekSettings)
            CalendarGrid(month, year, latitude, longitude, timeZone, weekSettings)
            CalendarFooter(month, year, latitude, longitude, timeZone, weekSettings)
        }
    }
}

@Composable
private fun CalendarHeader(month: Month, year: Int, weekSettings: WeekSettings) {
    val yearMonth = YearMonth(year, month)
    val titleString = yearMonth.toJavaYearMonth().format(DateTimeFormatter.ofPattern("MMMM, yyyy"))
    val daysOfWeek = (1..7).map { weekSettings.firstDayOfWeek.plus(IntDays(it - 1)) }
    val dayStrings = daysOfWeek.map { DateTimeFormatter.ofPattern("eeee").format(it.toJavaDayOfWeek()) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titleString, style = titleStyle)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = marginTitle, bottom = marginColumnHeader)
        ) {
            dayStrings.map {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(it, style = weekdayStyle)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.CalendarGrid(
    month: Month,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone,
    weekSettings: WeekSettings
) {
    val rows = getCalendarCellData(month, year, latitude, longitude, timeZone, weekSettings).chunked(7)
    Column(
        modifier = Modifier
            .border(strokeWidthOuter, Color.Black)
            .weight(rows.size.toFloat())
            .padding(strokeWidthOuter / 2)
    ) {
        rows.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                week.forEach {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f)
                            .border(strokeWidthInner / 2, Color.Black)
                            .padding(marginInternal),
                    ) {
                        CalendarCell(it)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCell(data: CalendarCellData?) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (data != null) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(data.date, style = dateStyle)
                Text(data.sunText1, style = timeStyle)
                Text(data.sunText2, style = timeStyle)
                Text(data.moonText1, style = timeStyle)
                Text(data.moonText2, style = timeStyle)
            }
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End) {
                if (data.dst != null) {
                    Text(data.dst, style = dstStyle)
                }
                if (data.phase != null) {
                    Text(
                        data.phase,
                        style = symbolStyle,
                        fontFamily = fontFamily(font("Symbola", "Symbola.ttf"))
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.CalendarFooter(
    month: Month,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone,
    weekSettings: WeekSettings
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(marginInternal + strokeWidthOuter / 2),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Times computed for ${latitude.latitudeString()}, ${longitude.longitudeString()} using timezone ${timeZone.id}",
            style = footerStyle
        )
        Text(
            "Copyright © 2021 Russell Wolf. All rights reserved",
            style = footerStyle
        )
    }
    // Footer spacing to match the rows we didn't need
    repeat(6 - Date(year, month, month.lastDayIn(year)).weekOfMonth(weekSettings)) {
        Row(modifier = Modifier.weight(1f)) {}
    }
}

data class CalendarCellData(
    val date: String,
    val sunText1: String,
    val sunText2: String,
    val moonText1: String,
    val moonText2: String,
    val phase: String?,
    val dst: String?
)

// TODO where should this live? (inside or outside of Calendar UI?)
private fun getCalendarCellData(
    month: Month,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone,
    weekSettings: WeekSettings
): List<CalendarCellData?> {
    val firstDayOfWeekNumber = Date(year, month, 1).dayOfWeek.number(weekSettings)
    val lastDayOfWeekNumber = Date(year, month, month.lastDayIn(year)).dayOfWeek.number(weekSettings)
    return List(firstDayOfWeekNumber - 1) { null } +
            month.dayRangeIn(year).map { day ->
                val date = Date(year, month, day)
                val (sunriseDate, sunsetDate) = sunTimes(date, timeZone, latitude, longitude)
                val (moonriseDate, moonsetDate) = moonTimes(date, timeZone, latitude, longitude)

                val sunriseText = "Sunrise: ${sunriseDate?.formatTime() ?: "None"}"
                val sunsetText = "Sunset: ${sunsetDate?.formatTime() ?: "None"}"

                val moonriseText = "Moonrise: ${moonriseDate?.formatTime() ?: "None"}"
                val moonsetText = "Moonset: ${moonsetDate?.formatTime() ?: "None"}"
                val isMoonriseFirst = (moonriseDate?.millisecondsSinceUnixEpoch ?: Long.MIN_VALUE.milliseconds) <
                        (moonsetDate?.millisecondsSinceUnixEpoch ?: Long.MAX_VALUE.milliseconds)

                val phase = when (moonPhase(date, timeZone, longitude)) {
                    MoonPhase.NEW -> "\uD83C\uDF11"
                    MoonPhase.FIRST_QUARTER -> "\uD83C\uDF13"
                    MoonPhase.FULL -> "\uD83C\uDF15"
                    MoonPhase.LAST_QUARTER -> "\uD83C\uDF17"
                    null -> null
                }

                val zoneOffsetStart = date.atTime(0, 0).at(timeZone).offset.totalSeconds
                val zoneOffsetEnd = date.atTime(23, 59, 59, 999999999).at(timeZone).offset.totalSeconds
                val dstText = when {
                    zoneOffsetStart > zoneOffsetEnd -> "DST ends"
                    zoneOffsetStart < zoneOffsetEnd -> "DST starts"
                    else -> null
                }

                CalendarCellData(
                    date = day.toString(),
                    sunText1 = sunriseText,
                    sunText2 = sunsetText,
                    moonText1 = if (isMoonriseFirst) moonriseText else moonsetText,
                    moonText2 = if (isMoonriseFirst) moonsetText else moonriseText,
                    phase = phase,
                    dst = dstText
                )
            } +
            List(7 - lastDayOfWeekNumber) { null }
}

private object CalendarTheme {
    // TODO there's a factor of 2 somewhere messing things up
    const val SCALE = 2.0 / 2

    val PAGE_WIDTH = (SCALE * 3300).roundToInt()
    val PAGE_HEIGHT = (SCALE * 2550).roundToInt()

    object Dimensions {

        val marginHorizontal = (SCALE * 200).dp
        val marginHeader = (SCALE * 550).dp
        val marginFooter = (SCALE * 250).dp
        val marginInternal = (SCALE * 15).dp
        val marginTitle = (SCALE * 50).dp
        val marginColumnHeader = (SCALE * 40).dp
        val strokeWidthInner = (SCALE * 10).dp
        val strokeWidthOuter = (SCALE * 15).dp
    }

    object Typography {
        private val baseFontFamily = fontFamily(font(alias = "Roboto-Medium", path = "Roboto-Medium.ttf"))
        private val symbolFontFamily = fontFamily(font(alias = "Symbola", path = "Symbola.ttf"))

        val titleStyle = TextStyle(fontFamily = baseFontFamily, fontSize = (SCALE * 144).sp)
        val symbolStyle = TextStyle(fontFamily = symbolFontFamily, fontSize = (SCALE * 48).sp)
        val dateStyle = TextStyle(fontFamily = baseFontFamily, fontSize = (SCALE * 64).sp)
        val weekdayStyle = TextStyle(fontFamily = baseFontFamily, fontSize = (SCALE * 72).sp)
        val timeStyle = TextStyle(fontFamily = baseFontFamily, fontSize = (SCALE * 36).sp)
        val dstStyle = timeStyle
        val footerStyle = timeStyle
    }
}

private fun ZonedDateTime.formatTime(): String =
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(toJavaZonedDateTime().toLocalTime())

private fun Double.latitudeString(): String = positionString(if (this < 0) "S" else "N")
private fun Double.longitudeString(): String = positionString(if (this < 0) "W" else "E")
private fun Double.positionString(direction: String): String = "%1.3f°$direction".format(abs(this))

// This helper doesn't exist in Island Time (yet?)
private fun DayOfWeek.toJavaDayOfWeek(): java.time.DayOfWeek = java.time.DayOfWeek.values()[ordinal]
