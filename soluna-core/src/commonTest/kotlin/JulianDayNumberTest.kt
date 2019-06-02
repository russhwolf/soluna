package com.russhwolf.soluna

import kotlin.test.Test
import kotlin.test.assertEquals

class JulianDayNumberTest {
    @Test
    fun julianDayNumberTest() {
        assertJulianDayNumber(2000, 1, 1, 2451545)
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
