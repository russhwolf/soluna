package com.russhwolf.soluna

import kotlin.test.Test
import kotlin.test.assertEquals

class JulianDayNumberTest {
    @Test
    fun julianDayNumberTest() {
        // Test cases adapted from Astronomical Algorithms pp 61-62
        // Note that for non-integer JD in AA examples, we round so that we have the JD for noon (D.5) on the given date
        assertTwoWayMatch(1957, 10, 4, 2436116)
        assertTwoWayMatch(333, 1, 27, 1842713, JulianCalendar)
        assertTwoWayMatch(2000, 1, 1, 2451545)
        assertTwoWayMatch(1999, 1, 1, 2451180)
        assertTwoWayMatch(1987, 1, 27, 2446823)
        assertTwoWayMatch(1987, 6, 19, 2446966)
        assertTwoWayMatch(1988, 1, 27, 2447188)
        assertTwoWayMatch(1988, 6, 19, 2447332)
        assertTwoWayMatch(1900, 1, 1, 2415021)
        assertTwoWayMatch(1600, 1, 1, 2305448)
        assertTwoWayMatch(1600, 12, 31, 2305813)
        assertTwoWayMatch(837, 4, 10, 2026872, JulianCalendar)
        assertTwoWayMatch(-123, 12, 31, 1676497, JulianCalendar)
        assertTwoWayMatch(-122, 1, 1, 1676498, JulianCalendar)
        assertTwoWayMatch(-1000, 7, 12, 1356001, JulianCalendar)
        assertTwoWayMatch(-1000, 2, 29, 1355867, JulianCalendar)
        assertTwoWayMatch(-1001, 8, 17, 1355671, JulianCalendar)
        assertTwoWayMatch(-4712, 1, 1, 0, JulianCalendar)
    }
}

private fun assertTwoWayMatch(
    year: Int,
    month: Int,
    day: Int,
    JD: Int,
    calendar: Calendar = GregorianCalendar
) {
    assertJulianDayNumber(
        year = year,
        month = month,
        day = day,
        expected = JD,
        calendar = calendar
    )
    assertYearMonthDay(
        JD = JD,
        expectedYear = year,
        expectedMonth = month,
        expectedDay = day,
        calendar = calendar
    )
}

private fun assertJulianDayNumber(
    year: Int,
    month: Int,
    day: Int,
    expected: Int,
    calendar: Calendar = GregorianCalendar
) {
    val actual = julianDayNumber(year, month, day, calendar)
    assertEquals(expected, actual, "Incorrect JD for $year/$month/$day")
}

private fun assertYearMonthDay(
    JD: Int,
    expectedYear: Int,
    expectedMonth: Int,
    expectedDay: Int,
    calendar: Calendar = GregorianCalendar
) {
    val (actualYear, actualMonth, actualDay) = dateFromJulianDayNumber(JD, calendar)
    assertEquals(expectedYear, actualYear, "Incorrect year for JD=$JD")
    assertEquals(expectedMonth, actualMonth, "Incorrect month for JD=$JD")
    assertEquals(expectedDay, actualDay, "Incorrect day for JD=$JD")
}
