package com.russhwolf.soluna.calendar

import com.russhwolf.soluna.calendar.CalendarRenderer.Companion.PAGE_HEIGHT
import com.russhwolf.soluna.calendar.CalendarRenderer.Companion.PAGE_WIDTH
import com.russhwolf.soluna.time.TimeAstronomicalCalculator
import io.islandtime.DayOfWeek
import io.islandtime.Month
import io.islandtime.TimeZone
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt

fun main(vararg args: String) {
    val locationName = args[0]
    val year = args[1].toInt()
    val latitude = args[2].toDouble()
    val longitude = args[3].toDouble()
    val timeZone = TimeZone(args[4])
    renderCalendars(locationName, year, latitude, longitude, timeZone)
}

private fun renderCalendars(
    locationName: String,
    year: Int,
    latitude: Double,
    longitude: Double,
    timeZone: TimeZone
) {
    val calendarDataGenerator = CalendarDataGenerator(
        IslandTimeCalendarDataHelper(timeZone),
        TimeAstronomicalCalculator.factory(timeZone, latitude, longitude)
    )
    val firstDayOfWeek = DayOfWeek.SUNDAY

    for (month in Month.entries) {
        val monthContent =
            calendarDataGenerator.generateCalendarMonth(year, month, firstDayOfWeek, latitude, longitude, timeZone)
                .toCalendarMonthContent()
        renderCalendarToFile(monthContent, locationName, month, year)
    }
}

internal fun renderCalendarToFile(
    monthContent: CalendarMonthContent,
    locationName: String,
    month: Month,
    year: Int
): File {
    val image = BufferedImage(PAGE_WIDTH.roundToInt(), PAGE_HEIGHT.roundToInt(), BufferedImage.TYPE_INT_RGB)
    image.createGraphics().run {
        renderCalendar(monthContent)
        dispose()
    }
    val file = File("$locationName-$year-${month.number}.png")
    ImageIO.write(image, "png", file)
    return file
}
