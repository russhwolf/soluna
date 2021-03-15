package com.russhwolf.soluna.calendar

import com.russhwolf.soluna.MoonPhase
import com.russhwolf.soluna.calendar.CalendarRenderer.Companion.PAGE_HEIGHT
import com.russhwolf.soluna.calendar.CalendarRenderer.Companion.PAGE_WIDTH
import com.russhwolf.soluna.time.moonPhase
import com.russhwolf.soluna.time.moonTimes
import com.russhwolf.soluna.time.sunTimes
import io.islandtime.Date
import io.islandtime.DayOfWeek
import io.islandtime.Instant
import io.islandtime.Month
import io.islandtime.TimeZone
import io.islandtime.YearMonth
import io.islandtime.at
import io.islandtime.atTime
import io.islandtime.calendar.WeekSettings
import io.islandtime.jvm.toJavaLocalDateTime
import io.islandtime.jvm.toJavaYearMonth
import io.islandtime.measures.IntDays
import io.islandtime.measures.milliseconds
import io.islandtime.toDateTimeAt
import io.islandtime.weekOfMonth
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.awt.image.BufferedImage
import java.io.File
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.roundToInt
import java.time.DayOfWeek as JavaDayOfWeek


fun renderCalendars(
    locationName: String,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone
) {
    for (month in Month.values()) {
        renderCalendarToFile(locationName, month, year, latitude, longitude, timeZone)
    }
}

internal fun renderCalendarToFile(
    locationName: String,
    month: Month,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone
): File {
    val image = BufferedImage(PAGE_WIDTH.roundToInt(), PAGE_HEIGHT.roundToInt(), BufferedImage.TYPE_INT_RGB)
    image.createGraphics().run {
        renderCalendar(month, year, latitude, longitude, timeZone)
        dispose()
    }
    val file = File("$locationName-$year-${month.number}.png")
    ImageIO.write(image, "png", file)
    return file
}

private fun Graphics2D.renderCalendar(
    month: Month,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone
) =
    CalendarRenderer(this, month, year, latitude, longitude, timeZone)

private class CalendarRenderer(
    graphics: Graphics2D,
    private val month: Month,
    private val year: Int,
    private val latitude: Double,
    private val longitude: Double,
    private val timeZone: TimeZone
) {
    private val innerStroke = BasicStroke(STROKE_WIDTH_INNER)
    private val outerStroke = BasicStroke(STROKE_WIDTH_OUTER)
    private val numberOfDays = month.lastDayIn(year)
    private val weekSettings = WeekSettings(DayOfWeek.SUNDAY, 1)
    private val lastDay = Date(year, month, numberOfDays)
    private val rows = lastDay.weekOfMonth(weekSettings)

    private val baseFont =
        Font.createFont(0, Thread.currentThread().contextClassLoader.getResourceAsStream("Roboto-Medium.ttf"))
    private val symbolFont =
        Font.createFont(0, Thread.currentThread().contextClassLoader.getResourceAsStream("Symbola.ttf"))
            .deriveScaledFont(48f)
    private val titleFont = baseFont.deriveScaledFont(144f)
    private val dateFont = baseFont.deriveScaledFont(64f)
    private val weekdayFont = baseFont.deriveScaledFont(72f)
    private val timeFont = baseFont.deriveScaledFont(36f)
    private val dstFont = timeFont
    private val footerFont = timeFont

    private fun Font.deriveScaledFont(size: Float) = deriveFont(SCALE.toFloat() * size)

    init {
        graphics.apply {
            background = Color.WHITE
            fillRect(0, 0, PAGE_WIDTH.roundToInt(), PAGE_HEIGHT.roundToInt())
            color = Color.BLACK
            drawGrid()
            drawCellContents()
            drawTitle()
            drawWeekdayLabels()
            drawLeftFooter()
            drawRightFooter()
        }
    }

    private fun Graphics2D.drawGrid() {
        for (i in 0..rows) {
            stroke = if (i == 0 || i == rows) outerStroke else innerStroke
            val y = MARGIN_HEADER + i * CELL_HEIGHT
            val line = Line2D.Double(MARGIN_HORIZONTAL, y, PAGE_WIDTH - MARGIN_HORIZONTAL, y)
            draw(line)
        }
        for (i in 0..COLUMNS) {
            stroke = if (i == 0 || i == COLUMNS) outerStroke else innerStroke
            val x = MARGIN_HORIZONTAL + i * CELL_WIDTH
            val line = Line2D.Double(x, MARGIN_HEADER, x, MARGIN_HEADER + rows * CELL_HEIGHT)
            draw(line)
        }
    }

    private fun Graphics2D.drawCellContents() {
        val previousTransform = transform
        for (i in 1..numberOfDays) {
            val date = Date(year, month, i)
            val (sunRiseDate, sunSetDate) = sunTimes(date, timeZone, latitude, longitude)
            val (moonRiseDate, moonSetDate) = moonTimes(date, timeZone, latitude, longitude)
            val weekDayValue = date.dayOfWeek.number(weekSettings)
            val weekValue = date.weekOfMonth(weekSettings)

            font = dateFont
            translate(cellX(weekDayValue), cellY(weekValue) + fontMetrics.ascent)
            drawString("$i", 0, 0)
            font = timeFont
            translate(0.0, MARGIN_INTERNAL + fontMetrics.ascent)
            drawString("Sunrise: ${sunRiseDate?.formatTime() ?: "None"}", 0, 0)
            translate(0.0, MARGIN_INTERNAL / 2 + fontMetrics.ascent)
            drawString("Sunset: ${sunSetDate?.formatTime() ?: "None"}", 0, 0)
            if (
                (moonRiseDate?.millisecondsSinceUnixEpoch ?: Long.MIN_VALUE.milliseconds)
                <
                (moonSetDate?.millisecondsSinceUnixEpoch ?: Long.MAX_VALUE.milliseconds)
            ) {
                translate(0.0, MARGIN_INTERNAL / 2 + fontMetrics.ascent)
                drawString("Moonrise: ${moonRiseDate?.formatTime() ?: "None"}", 0, 0)
                translate(0.0, MARGIN_INTERNAL / 2 + fontMetrics.ascent)
                drawString("Moonset: ${moonSetDate?.formatTime() ?: "None"}", 0, 0)
            } else {
                translate(0.0, MARGIN_INTERNAL / 2 + fontMetrics.ascent)
                drawString("Moonset: ${moonSetDate?.formatTime() ?: "None"}", 0, 0)
                translate(0.0, MARGIN_INTERNAL / 2 + fontMetrics.ascent)
                drawString("Moonrise: ${moonRiseDate?.formatTime() ?: "None"}", 0, 0)
            }
            transform = previousTransform

            val phase = when (moonPhase(date, timeZone, longitude)) {
                MoonPhase.NEW -> "\uD83C\uDF11"
                MoonPhase.FIRST_QUARTER -> "\uD83C\uDF13"
                MoonPhase.FULL -> "\uD83C\uDF15"
                MoonPhase.LAST_QUARTER -> "\uD83C\uDF17"
                null -> null
            }
            if (phase != null) {
                font = symbolFont
                translate(
                    cellX(weekDayValue) + CELL_WIDTH - fontMetrics.stringWidth(phase) - 2 * MARGIN_INTERNAL,
                    cellY(weekValue) + fontMetrics.ascent
                )
                drawString(phase, 0, 0)
                transform = previousTransform
            }

            val zoneOffsetStart = date.atTime(0, 0).at(timeZone).offset.totalSeconds
            val zoneOffsetEnd = date.atTime(23, 59, 59, 999999999).at(timeZone).offset.totalSeconds
            val dstString = when {
                zoneOffsetStart > zoneOffsetEnd -> "DST ends"
                zoneOffsetStart < zoneOffsetEnd -> "DST starts"
                else -> ""
            } + if (phase != null) "\u2002\u2003" else ""
            if (dstString.isNotBlank()) {
                font = dstFont
                translate(
                    cellX(weekDayValue) + CELL_WIDTH - fontMetrics.stringWidth(dstString) - 2 * MARGIN_INTERNAL,
                    cellY(weekValue) + fontMetrics.ascent
                )
                drawString(dstString, 0, 0)
                transform = previousTransform
            }

        }
    }

    private fun Graphics2D.drawTitle() {
        font = titleFont
        val yearMonth = YearMonth(year, month)
        val titleString = yearMonth.toJavaYearMonth().format(DateTimeFormatter.ofPattern("MMMM, yyyy"))
        val stringWidth = fontMetrics.stringWidth(titleString)
        drawString(
            titleString,
            (PAGE_WIDTH - stringWidth).toFloat() / 2,
            (MARGIN_HEADER - MARGIN_TITLE - getFontMetrics(weekdayFont).ascent - MARGIN_COLUMN_HEADER).toFloat()
        )
    }

    private fun Graphics2D.drawWeekdayLabels() {
        font = weekdayFont
        val previousTransform = transform
        val formatter = DateTimeFormatter.ofPattern("eeee")
        translate(MARGIN_HORIZONTAL, MARGIN_HEADER - MARGIN_COLUMN_HEADER)
        for (i in 1..COLUMNS) {
            val dayOfWeek = weekSettings.firstDayOfWeek.plus(IntDays(i - 1))
            val dayString = formatter.format(dayOfWeek.toJavaDayOfWeek())
            val stringWidth = fontMetrics.stringWidth(dayString)
            drawString(dayString, CELL_WIDTH.toFloat() * (i - 0.5f) - stringWidth / 2, 0f)
        }
        transform = previousTransform
    }

    private fun Graphics2D.drawLeftFooter() {
        font = footerFont
        val previousTransform = transform
        translate(
            MARGIN_HORIZONTAL + MARGIN_INTERNAL,
            MARGIN_HEADER + rows * CELL_HEIGHT + fontMetrics.ascent + MARGIN_INTERNAL
        )
        drawString(FOOTER_LEFT_FORMAT.format(latitude.latitudeString(), longitude.longitudeString(), timeZone.id), 0, 0)
        transform = previousTransform
    }

    private fun Graphics2D.drawRightFooter() {
        font = footerFont
        val previousTransform = transform
        val stringWidth = fontMetrics.stringWidth(FOOTER_RIGHT)
        translate(
            PAGE_WIDTH - MARGIN_HORIZONTAL - stringWidth - MARGIN_INTERNAL,
            MARGIN_HEADER + rows * CELL_HEIGHT + fontMetrics.ascent + MARGIN_INTERNAL
        )
        drawString(FOOTER_RIGHT, 0, 0)
        transform = previousTransform
    }

    private fun cellX(dayOfWeek: Int) = MARGIN_HORIZONTAL + CELL_WIDTH * (dayOfWeek - 1) + MARGIN_INTERNAL
    private fun cellY(weekOfMonth: Int) = MARGIN_HEADER + CELL_HEIGHT * (weekOfMonth - 1) + MARGIN_INTERNAL

    private fun Instant.formatTime(): String {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(toDateTimeAt(timeZone).toJavaLocalDateTime())
    }

    private fun Double.latitudeString(): String = positionString(if (this < 0) "S" else "N")
    private fun Double.longitudeString(): String = positionString(if (this < 0) "W" else "E")
    private fun Double.positionString(direction: String): String = "%1.3f°$direction".format(abs(this))

    companion object {
        private const val SCALE = 2.0
        internal const val PAGE_WIDTH = SCALE * 3300.0
        internal const val PAGE_HEIGHT = SCALE * 2550.0
        private const val MARGIN_HORIZONTAL = SCALE * 200.0
        private const val MARGIN_HEADER = SCALE * 550.0
        private const val MARGIN_FOOTER = SCALE * 250.0
        private const val MARGIN_INTERNAL = SCALE * 15.0
        private const val MARGIN_TITLE = SCALE * 50.0
        private const val MARGIN_COLUMN_HEADER = SCALE * 40.0
        private const val ROWS = 6
        private const val COLUMNS = 7
        private const val STROKE_WIDTH_INNER = SCALE.toFloat() * 10f
        private const val STROKE_WIDTH_OUTER = SCALE.toFloat() * 15f

        private const val CELL_WIDTH = (PAGE_WIDTH - 2 * MARGIN_HORIZONTAL) / COLUMNS
        private const val CELL_HEIGHT = (PAGE_HEIGHT - (MARGIN_HEADER + MARGIN_FOOTER)) / ROWS

        private const val FOOTER_LEFT_FORMAT = "Times computed for %s, %s using timezone %s"
        private const val FOOTER_RIGHT = "Copyright © 2020 Russell Wolf. All rights reserved"
    }
}

// This helper doesn't exist in Island Time (yet?)
private fun DayOfWeek.toJavaDayOfWeek(): JavaDayOfWeek = JavaDayOfWeek.values()[ordinal]
