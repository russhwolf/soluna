package com.russhwolf.soluna.calendar

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
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.ZoneId
import java.time.temporal.WeekFields
import javax.imageio.ImageIO
import kotlin.math.floor
import kotlin.math.roundToInt

fun main() {
    val month = Month.JULY
    val year = 2019
    val latitude = 42.3875968
    val longitude = -71.0994968
    val timeZone = ZoneId.of("America/New_York")

    val image = BufferedImage(PAGE_WIDTH.roundToInt(), PAGE_HEIGHT.roundToInt(), BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.apply {
        background = Color.WHITE
        fillRect(0, 0, PAGE_WIDTH.roundToInt(), PAGE_HEIGHT.roundToInt())
        color = Color.BLACK
        drawCalendarGrid(month, year, latitude, longitude, timeZone)
    }
    graphics.dispose()
    val file = File("CalendarTest.png")
    ImageIO.write(image, "png", file)
}

private fun Graphics2D.drawCalendarGrid(
    month: Month,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: ZoneId
) {
    val innerStroke = BasicStroke(STROKE_WIDTH_INNER)
    val outerStroke = BasicStroke(STROKE_WIDTH_OUTER)

    val numberOfDays = month.length(Year.isLeap(year.toLong()))
    val weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1)
    val lastDay = LocalDate.of(year, month, numberOfDays)
    val rows = lastDay.get(weekFields.weekOfMonth())

    val previousTransform = transform

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
    for (i in 1..numberOfDays) {
        val date = LocalDate.of(year, month, i)
        val weekDayValue = date.get(weekFields.dayOfWeek())
        val weekValue = date.get(weekFields.weekOfMonth())
        // TODO this offset does not correctly take into account the time of DST change
        val offset = timeZone.rules.getOffset(LocalDateTime.of(year, month, i, 12, 0)).totalSeconds / 3600.0
        val (riseTime, setTime) = sunTimes(year, month.value, i, offset, latitude, longitude)

        font = FONT_BASE.deriveFont(FONT_SIZE_DATE)
        translate(cellX(weekDayValue), cellY(weekValue) + fontMetrics.ascent)
        drawString("$i", 0, 0)
        font = FONT_BASE.deriveFont(FONT_SIZE_TIME)
        translate(0.0, CELL_MARGIN * 2 + fontMetrics.ascent)
        drawString("Sunrise: ${riseTime?.timeString() ?: "None"}", 0, 0)
        translate(0.0, CELL_MARGIN / 2 + fontMetrics.ascent)
        drawString("Sunset: ${setTime?.timeString() ?: "None"}", 0, 0)
        transform = previousTransform
    }
}

private fun cellX(dayOfWeek: Int) = MARGIN_HORIZONTAL + CELL_WIDTH * (dayOfWeek - 1) + CELL_MARGIN
private fun cellY(weekOfMonth: Int) = MARGIN_HEADER + CELL_HEIGHT * (weekOfMonth - 1) + CELL_MARGIN
private fun Double.timeString(): String {
    val hours = floor(this).toInt()
    val minutes = ((this - hours) * 60).roundToInt()

    return "%02d:%02d".format(hours, minutes)
}

private const val PAGE_WIDTH = 3300.0
private const val PAGE_HEIGHT = 2550.0
private const val MARGIN_HORIZONTAL = 200.0
private const val MARGIN_HEADER = 500.0
private const val MARGIN_FOOTER = 300.0
private const val ROWS = 6
private const val COLUMNS = 7
private const val STROKE_WIDTH_INNER = 10f
private const val STROKE_WIDTH_OUTER = 15f
private const val CELL_MARGIN = 15.0
private const val FONT_SIZE_DATE = 48f
private const val FONT_SIZE_TIME = 36f

private const val CELL_WIDTH = (PAGE_WIDTH - 2 * MARGIN_HORIZONTAL) / COLUMNS
private const val CELL_HEIGHT = (PAGE_HEIGHT - (MARGIN_HEADER + MARGIN_FOOTER)) / ROWS

private val FONT_BASE =
    Font.createFont(0, Thread.currentThread().contextClassLoader.getResourceAsStream("Roboto-Medium.ttf"))
