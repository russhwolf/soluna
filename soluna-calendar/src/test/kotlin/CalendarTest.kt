package com.russhwolf.soluna.calendar

import io.islandtime.Month
import io.islandtime.TimeZone
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Integration tests to track calendar drift
 */
// TODO find a less flaky way to do this
@Ignore("Flaky")
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
     * No Sunrise
     *
     * This wouldn't be the correct time-zone in practice but it works as a rendering test-case
     */
    @Test
    fun east_base_2020_11() = assertMatchingCalendar(
        filename = "East-Base-2020-11.png",
        locationName = "East-Base",
        month = Month.NOVEMBER,
        year = 2020,
        latitude = -68.183841,
        longitude = -66.998158,
        timeZone = TimeZone("America/New_York")
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
        val actualFile = renderCalendarToFile(locationName, month, year, latitude, longitude, timeZone)

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
            actualFile.delete()
        }

    }
}
