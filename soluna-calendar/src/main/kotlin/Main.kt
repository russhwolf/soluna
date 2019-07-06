package com.russhwolf.soluna.calendar

import java.awt.BasicStroke
import java.awt.Color
import java.awt.geom.Line2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt

fun main() {
    val image = BufferedImage(PAGE_WIDTH.roundToInt(), PAGE_HEIGHT.roundToInt(), BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.apply {
        background = Color.WHITE
        fillRect(0, 0, PAGE_WIDTH.roundToInt(), PAGE_HEIGHT.roundToInt())

        color = Color.BLACK
        val innerStroke = BasicStroke(STROKE_WIDTH_INNER)
        val outerStroke = BasicStroke(STROKE_WIDTH_OUTER)
        for (i in 0..ROWS) {
            stroke = if (i == 0 || i == ROWS) outerStroke else innerStroke
            val y = MARGIN_HEADER + i * CELL_HEIGHT
            val line = Line2D.Double(MARGIN_HORIZONTAL, y, PAGE_WIDTH - MARGIN_HORIZONTAL, y)
            draw(line)
        }
        for (i in 0..COLUMNS) {
            stroke = if (i == 0 || i == COLUMNS) outerStroke else innerStroke
            val x = MARGIN_HORIZONTAL + i * CELL_WIDTH
            val line = Line2D.Double(x, MARGIN_HEADER, x, PAGE_HEIGHT - MARGIN_FOOTER)
            draw(line)
        }
    }
    graphics.dispose()
    val file = File("CalendarTest.png")
    ImageIO.write(image, "png", file)
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

private const val CELL_WIDTH = (PAGE_WIDTH - 2 * MARGIN_HORIZONTAL) / COLUMNS
private const val CELL_HEIGHT = (PAGE_HEIGHT - (MARGIN_HEADER + MARGIN_FOOTER)) / ROWS
