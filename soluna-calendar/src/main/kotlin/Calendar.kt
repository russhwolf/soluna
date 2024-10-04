package com.russhwolf.soluna.calendar

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Line2D
import kotlin.math.roundToInt

internal fun Graphics2D.renderCalendar(monthContent: CalendarMonthContent) = CalendarRenderer(this, monthContent)

internal class CalendarRenderer(
    graphics: Graphics2D,
    private val monthContent: CalendarMonthContent
) {
    private val rows = monthContent.weeks.size
    private val innerStroke = BasicStroke(STROKE_WIDTH_INNER)
    private val outerStroke = BasicStroke(STROKE_WIDTH_OUTER)

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

        for ((i, weekContent) in monthContent.weeks.withIndex()) {
            for ((j, cellContent) in weekContent.days.withIndex()) {

                when (cellContent) {
                    is CalendarCellContent.Empty -> {}
                    is CalendarCellContent.Content -> {
                        val weekValue = i + 1
                        val weekDayValue = j + 1

                        font = dateFont
                        translate(cellX(weekDayValue), cellY(weekValue) + fontMetrics.ascent)
                        drawString(cellContent.date, 0, 0)
                        font = timeFont

                        translate(0.0, MARGIN_INTERNAL + fontMetrics.ascent)
                        for (line in cellContent.sunText.lines() + cellContent.moonText.lines()) {
                            drawString(line, 0, 0)
                            translate(0.0, MARGIN_INTERNAL / 2 + fontMetrics.ascent)
                        }

                        transform = previousTransform

                        font = symbolFont
                        translate(
                            cellX(weekDayValue) + CELL_WIDTH - fontMetrics.stringWidth(cellContent.moonPhase) - 2 * MARGIN_INTERNAL,
                            cellY(weekValue) + fontMetrics.ascent
                        )
                        drawString(cellContent.moonPhase, 0, 0)
                        transform = previousTransform

                        font = dstFont
                        translate(
                            cellX(weekDayValue) + CELL_WIDTH - fontMetrics.stringWidth(cellContent.dst) - 2 * MARGIN_INTERNAL,
                            cellY(weekValue) + fontMetrics.ascent
                        )
                        drawString(cellContent.dst, 0, 0)
                        transform = previousTransform
                    }
                }
            }
        }
    }

    private fun Graphics2D.drawTitle() {
        font = titleFont
        val stringWidth = fontMetrics.stringWidth(monthContent.title)
        drawString(
            monthContent.title,
            (PAGE_WIDTH - stringWidth).toFloat() / 2,
            (MARGIN_HEADER - MARGIN_TITLE - getFontMetrics(weekdayFont).ascent - MARGIN_COLUMN_HEADER).toFloat()
        )
    }

    private fun Graphics2D.drawWeekdayLabels() {
        font = weekdayFont
        val previousTransform = transform
        translate(MARGIN_HORIZONTAL, MARGIN_HEADER - MARGIN_COLUMN_HEADER)
        for ((i, dayString) in monthContent.daysOfWeek.withIndex()) {
            val stringWidth = fontMetrics.stringWidth(dayString)
            drawString(dayString, CELL_WIDTH.toFloat() * (i + 0.5f) - stringWidth / 2, 0f)
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
        drawString(monthContent.locationText, 0, 0)
        transform = previousTransform
    }

    private fun Graphics2D.drawRightFooter() {
        font = footerFont
        val previousTransform = transform
        val stringWidth = fontMetrics.stringWidth(monthContent.aboutText)
        translate(
            PAGE_WIDTH - MARGIN_HORIZONTAL - stringWidth - MARGIN_INTERNAL,
            MARGIN_HEADER + rows * CELL_HEIGHT + fontMetrics.ascent + MARGIN_INTERNAL
        )
        drawString(monthContent.aboutText, 0, 0)
        transform = previousTransform
    }

    private fun cellX(dayOfWeek: Int) = MARGIN_HORIZONTAL + CELL_WIDTH * (dayOfWeek - 1) + MARGIN_INTERNAL
    private fun cellY(weekOfMonth: Int) = MARGIN_HEADER + CELL_HEIGHT * (weekOfMonth - 1) + MARGIN_INTERNAL

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
    }
}

