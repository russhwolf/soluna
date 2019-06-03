package com.russhwolf.soluna

import kotlin.test.Test
import kotlin.test.assertEquals

class JulianDayNumberTest {
    @Test
    fun julianDayNumberTest() {
        // Test cases adapted from Astronomical Algorithms pp 61-62
        assertJulianDayNumber(1957, 10, 4, 2436116)
        assertJulianDayNumber(333, 1, 27, 1842713, JulianCalendar)
        assertJulianDayNumber(2000, 1, 1, 2451545)
        assertJulianDayNumber(1999, 1, 1, 2451180)
        assertJulianDayNumber(1987, 1, 27, 2446823)
        assertJulianDayNumber(1987, 6, 19, 2446966)
        assertJulianDayNumber(1988, 1, 27, 2447188)
        assertJulianDayNumber(1988, 6, 19, 2447332)
        assertJulianDayNumber(1900, 1, 1, 2415021)
        assertJulianDayNumber(1600, 1, 1, 2305448)
        assertJulianDayNumber(1600, 12, 31, 2305813)
        assertJulianDayNumber(837, 4, 10, 2026872, JulianCalendar)
        assertJulianDayNumber(-123, 12, 31, 1676497, JulianCalendar)
        assertJulianDayNumber(-122, 1, 1, 1676498, JulianCalendar)
        assertJulianDayNumber(-1000, 7, 12, 1356001, JulianCalendar)
        assertJulianDayNumber(-1000, 2, 29, 1355867, JulianCalendar)
        assertJulianDayNumber(-1001, 8, 17, 1355671, JulianCalendar)
        assertJulianDayNumber(-4712, 1, 1, 0, JulianCalendar)
    }
}

private fun assertJulianDayNumber(
    year: Int,
    month: Int,
    day: Int,
    expected: Int,
    calendar: Calendar = GregorianCalendar
) {
    val actual = julianDayNumber(year, month, day, calendar)
    assertEquals(expected, actual, "$year/$month/$day: expected<$expected>, actual<$actual>")
}
