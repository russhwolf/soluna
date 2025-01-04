// Using names to match references (either Explanatory Supplement to the Astronomical Almanac, or others noted inline),
// so don't warn on naming
@file:Suppress("LocalVariableName", "FloatingPointLiteralPrecision")

package com.russhwolf.soluna

import com.russhwolf.soluna.math.HourAngle
import com.russhwolf.soluna.math.TAU
import com.russhwolf.soluna.math.deg
import com.russhwolf.soluna.math.div
import com.russhwolf.soluna.math.hour
import com.russhwolf.soluna.math.minus
import com.russhwolf.soluna.math.rad
import com.russhwolf.soluna.math.times
import com.russhwolf.soluna.math.toDegrees
import com.russhwolf.soluna.test.assertNearEquals
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO better testing needed here
class AstronomicalCalculatorTest {

    // TODO add some test cases from web if there's nothing good in reference books

    @Test
    fun solarEphemerisTest() {
        // Test case adapted from Astronomical Algorithms, Example 25.a (p. 165)
        val JD = julianDayNumber(1992, 10, 13)
        val UT = 0.hour

        val (GHA, delta) = solarEphemeris(JD, UT)
        val t_u = (JD - 0.5 + UT / HourAngle.MAX - 2_451_545.0)
        val Theta = TAU * (0.779_057_273_264_0 + 1.002_737_811_911_354_48 * t_u).rad
        val alpha = (Theta.toDegrees() - GHA).coerceInRange()

        assertNearEquals(
            expected = 198.38083,
            actual = alpha.value,
            tolerance = 5e-1
        )
        assertNearEquals(
            expected = -7.78507,
            actual = delta.value,
            tolerance = 5e-3
        )
    }

    @Test
    fun lunarEphemerisTest() {
        // Test case adapted from Astronomical Algorithms, Example 47.a (p. 342-343)
        val JD = julianDayNumber(1992, 4, 12)
        val UT = 0.hour

        val (GHA, delta) = lunarEphemeris(JD, UT, 0.deg, 0.deg)
        val t_u = (JD - 0.5 + UT / HourAngle.MAX - 2_451_545.0)
        val Theta = TAU * (0.779_057_273_264_0 + 1.002_737_811_911_354_48 * t_u).rad
        val alpha = (Theta.toDegrees() - GHA).coerceInRange()

        assertNearEquals(
            expected = 134.688470,
            actual = alpha.value,
            tolerance = 2e1 // test case is geocentric, hence large lateral error
        )
        assertNearEquals(
            expected = 13.768368,
            actual = delta.value,
            tolerance = 2e-1
        )
    }

    @Test
    fun moonPhaseTest() {
        // Test case adapted from Astronomical Algorithms, Example 49.a (p. 353)
        assertEquals(MoonPhase.NEW, MillisTimeAstronomicalCalculator(1977, 2, 18, 0.0, 0.0, 0.0).moonPhase)
    }
}
