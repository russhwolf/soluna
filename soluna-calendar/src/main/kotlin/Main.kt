package com.russhwolf.soluna.calendar

import com.russhwolf.soluna.calendar.CalendarRenderer.Companion.PAGE_HEIGHT
import com.russhwolf.soluna.calendar.CalendarRenderer.Companion.PAGE_WIDTH
import com.russhwolf.soluna.sunTimes
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.awt.image.BufferedImage
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.WeekFields
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

fun main() {
    val month = Month.JULY
    val year = 2019
    val latitude = 42.3875968
    val longitude = -71.0994968
    val timeZone = ZoneId.of("America/New_York")

    val image = BufferedImage(PAGE_WIDTH.roundToInt(), PAGE_HEIGHT.roundToInt(), BufferedImage.TYPE_INT_RGB)
    image.createGraphics().run {
        renderCalendar(month, year, latitude, longitude, timeZone)
        dispose()
    }
    val file = File("CalendarTest.png")
    ImageIO.write(image, "png", file)
}

private fun Graphics2D.renderCalendar(month: Month, year: Int, latitude: Double, longitude: Double, timeZone: ZoneId) =
    CalendarRenderer(this, month, year, latitude, longitude, timeZone)

private class CalendarRenderer(
    graphics: Graphics2D,
    private val month: Month,
    private val year: Int,
    private val latitude: Double,
    private val longitude: Double,
    private val timeZone: ZoneId
) {
    private val innerStroke = BasicStroke(STROKE_WIDTH_INNER)
    private val outerStroke = BasicStroke(STROKE_WIDTH_OUTER)
    private val numberOfDays = month.length(Year.isLeap(year.toLong()))
    private val weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1)
    private val lastDay = LocalDate.of(year, month, numberOfDays)
    private val rows = lastDay.get(weekFields.weekOfMonth())

    private val baseFont =
        Font.createFont(0, Thread.currentThread().contextClassLoader.getResourceAsStream("Roboto-Medium.ttf"))
    private val titleFont = baseFont.deriveFont(144f)
    private val dateFont = baseFont.deriveFont(64f)
    private val weekdayFont = baseFont.deriveFont(72f)
    private val timeFont = baseFont.deriveFont(36f)
    private val footerFont = timeFont

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
            val date = LocalDate.of(year, month, i)
            val (sunRiseTime, sunSetTime) = sunTimes(year, month.value, i, 0.0, latitude, longitude)
            val sunRiseDate = sunRiseTime?.toDateTime(date, timeZone)
            val sunSetDate = sunSetTime?.toDateTime(date, timeZone)
//            val (moonRiseTime, moonSetTime) = moonTimes(year, month.value, i, 0.0, latitude, longitude)
//            val moonRiseDate = moonRiseTime?.toDateTime(date, timeZone)
//            val moonSetDate = moonSetTime?.toDateTime(date, timeZone)

            val weekDayValue = date.get(weekFields.dayOfWeek())
            val weekValue = date.get(weekFields.weekOfMonth())

            font = dateFont
            translate(cellX(weekDayValue), cellY(weekValue) + fontMetrics.ascent)
            drawString("$i", 0, 0)
            font = timeFont
            translate(0.0, MARGIN_INTERNAL + fontMetrics.ascent)
            drawString("Sunrise: ${sunRiseDate?.formatTime() ?: "None"}", 0, 0)
            translate(0.0, MARGIN_INTERNAL / 2 + fontMetrics.ascent)
            drawString("Sunset: ${sunSetDate?.formatTime() ?: "None"}", 0, 0)
//            translate(0.0, MARGIN_INTERNAL / 2 + fontMetrics.ascent)
//            drawString("Moonrise: ${moonRiseDate?.formatTime() ?: "None"}", 0, 0)
//            translate(0.0, MARGIN_INTERNAL / 2 + fontMetrics.ascent)
//            drawString("Moonset: ${moonSetDate?.formatTime() ?: "None"}", 0, 0)
            transform = previousTransform
        }
    }

    private fun Graphics2D.drawTitle() {
        font = titleFont
        val yearMonth = YearMonth.of(year, month)
        val titleString = yearMonth.format(DateTimeFormatter.ofPattern("MMMM, yyyy"))
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
            val dayOfWeek = weekFields.firstDayOfWeek.plus((i - 1).toLong())
            val dayString = formatter.format(dayOfWeek)
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
    private fun Double.toDateTime(localDate: LocalDate, timeZone: ZoneId): ZonedDateTime {
        val hours = floor(this).toInt()
        val minutes = ((this - hours) * 60).roundToInt()
        val utcDateTime = ZonedDateTime.of(localDate, LocalTime.of(hours, minutes), ZoneId.of("UTC"))
        return utcDateTime.withZoneSameInstant(timeZone)
    }

    private fun ZonedDateTime.formatTime(): String {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(toLocalTime())
    }

    private fun Double.latitudeString(): String = positionString(if (this < 0) "S" else "N")
    private fun Double.longitudeString(): String = positionString(if (this < 0) "W" else "E")
    private fun Double.positionString(direction: String): String = "%1.3f°$direction".format(abs(this))

    companion object {
        internal const val PAGE_WIDTH = 3300.0
        internal const val PAGE_HEIGHT = 2550.0
        private const val MARGIN_HORIZONTAL = 200.0
        private const val MARGIN_HEADER = 550.0
        private const val MARGIN_FOOTER = 250.0
        private const val MARGIN_INTERNAL = 15.0
        private const val MARGIN_TITLE = 50.0
        private const val MARGIN_COLUMN_HEADER = 40.0
        private const val ROWS = 6
        private const val COLUMNS = 7
        private const val STROKE_WIDTH_INNER = 10f
        private const val STROKE_WIDTH_OUTER = 15f

        private const val CELL_WIDTH = (PAGE_WIDTH - 2 * MARGIN_HORIZONTAL) / COLUMNS
        private const val CELL_HEIGHT = (PAGE_HEIGHT - (MARGIN_HEADER + MARGIN_FOOTER)) / ROWS

        private const val FOOTER_LEFT_FORMAT = "Times computed for %s, %s using timezone %s"
        private const val FOOTER_RIGHT = "Copyright © 2019 Russell Wolf. All rights reserved"
    }
}
