package com.russhwolf.soluna.calendar

import com.russhwolf.soluna.time.TimeAstronomicalCalculator
import io.islandtime.Date
import io.islandtime.DayOfWeek
import io.islandtime.Month
import io.islandtime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Integration tests to track calendar drift
 */
// TODO find a less flaky way to do this
//@Ignore("Flaky")
class CalendarTest {

    /**
     * Non-UTC
     * DST
     * 6 weeks
     */
    @Test
    fun somerville_2019_03() = assertMatchingCalendar(
        filename = "Somerville-2019-3.png",
        locationName = "Somerville",
        month = Month.MARCH,
        year = 2019,
        latitude = 42.3875,
        longitude = -71.1,
        timeZone = TimeZone("America/New_York")
    )

    /**
     * Four-week February
     * UTC
     */
    @Test
    fun greenwich_2015_02() = assertMatchingCalendar(
        filename = "Greenwich-2015-2.png",
        locationName = "Greenwich",
        month = Month.FEBRUARY,
        year = 2015,
        latitude = 51.48,
        longitude = 0.0,
        timeZone = TimeZone("UTC")
    )

    /**
     * DST overlapping moon phase
     * No Moonrise/set
     */
    @Test
    fun troll_2020_03() = assertMatchingCalendar(
        filename = "Troll-2023-10.png",
        locationName = "Troll",
        month = Month.OCTOBER,
        year = 2023,
        latitude = -72.0121236,
        longitude = 2.5240873,
        timeZone = TimeZone("Antarctica/Troll")
    )

    private fun assertMatchingCalendar(
        filename: String,
        locationName: String,
        month: Month,
        year: Int,
        latitude: Double,
        longitude: Double,
        timeZone: TimeZone
    ) {
        val calendarDataGenerator = CalendarDataGenerator(IslandTimeCalendarDataHelper(timeZone)) { year, month, day ->
            TimeAstronomicalCalculator(Date(year, month, day), timeZone, latitude, longitude)
        }
        val firstDayOfWeek = DayOfWeek.SUNDAY
        val monthContent =
            calendarDataGenerator.generateCalendarMonth(year, month, firstDayOfWeek, latitude, longitude, timeZone)
                .toCalendarMonthContent()

        val actualFile = renderCalendarToFile(monthContent, locationName, month, year)

        try {
            val expected = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)
                ?: fail("Failed to read resource $filename")

            assertEquals(filename, actualFile.name)

            val actualStream = actualFile.inputStream()

            val bufferSize = 10 * 1024 * 1024
            val expectedBuffer = ByteArray(bufferSize)
            val actualBuffer = ByteArray(bufferSize)
            while (true) {
                val expectedBytes = expected.read(expectedBuffer)
                val actualBytes = actualStream.read(actualBuffer)
                assertEquals(expectedBytes, actualBytes)
                if (actualBytes < 0) break
                assertTrue(
                    actualBuffer.sliceArray(0 until actualBytes)
                        .contentEquals(expectedBuffer.sliceArray(0 until expectedBytes))
                )
            }
        } finally {
//            actualFile.delete()
        }

    }
}
