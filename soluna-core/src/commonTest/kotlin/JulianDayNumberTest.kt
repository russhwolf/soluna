package com.russhwolf.soluna

import kotlin.test.Test
import kotlin.test.assertEquals

class JulianDayNumberTest {
    @Test
    fun julianDayNumberTest() {
        // Test cases adapted from Astronomical Algorithms pp 61-62
        assertJulianDayNumber(1957, 10, 4, 2436115)
        assertJulianDayNumber(333, 1, 27, 1842712, JulianCalendar)
        assertJulianDayNumber(2000, 1, 1, 2451544)
        assertJulianDayNumber(1999, 1, 1, 2451179)
        assertJulianDayNumber(1987, 1, 27, 2446822)
        assertJulianDayNumber(1987, 6, 19, 2446965)
        assertJulianDayNumber(1988, 1, 27, 2447187)
        assertJulianDayNumber(1988, 6, 19, 2447331)
        assertJulianDayNumber(1900, 1, 1, 2415020)
        assertJulianDayNumber(1600, 1, 1, 2305447)
        assertJulianDayNumber(1600, 12, 31, 2305812)
        assertJulianDayNumber(837, 4, 10, 2026871, JulianCalendar)
        assertJulianDayNumber(-123, 12, 31, 1676496, JulianCalendar)
        assertJulianDayNumber(-122, 1, 1, 1676497, JulianCalendar)
        assertJulianDayNumber(-1000, 7, 12, 1356000, JulianCalendar)
        assertJulianDayNumber(-1000, 2, 29, 1355866, JulianCalendar)
        assertJulianDayNumber(-1001, 8, 17, 1355670, JulianCalendar)
        assertJulianDayNumber(-4712, 1, 1, -1, JulianCalendar)
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
