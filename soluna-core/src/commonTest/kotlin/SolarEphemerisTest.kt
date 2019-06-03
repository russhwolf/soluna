package com.russhwolf.soluna

import com.russhwolf.math.hour
import com.russhwolf.test.assertNearEquals
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.test.Ignore
import kotlin.test.Test

class SolarEphemerisTest {
    @Test
    fun hello() {
        sunTimes(
            year = 2019,
            month = 6,
            day = 2,
            offset = -4.0,
            latitude = 42.3875968,
            longitude = -71.0994968
        ).toList().forEach { it.printTime() }

        moonTimes(
            year = 2019,
            month = 6,
            day = 2,
            offset = -4.0,
            latitude = 42.3875968,
            longitude = -71.0994968
        ).toList().forEach { it.printTime() }
    }

    private fun Double.printTime() {
        val hours = floor(this).toInt()
        val minutes = ((this - hours) * 60).roundToInt()

        println("$hours:$minutes")
    }

    @Ignore
    @Test
    fun solarEphemerisTest() {
        // Test case adapted from Astronomical Algorithms, Example 25.a (p. 165)
        // TODO why doesn't it work? Need to translate GHA and right ascension
        val JD = 2448908
        val UT = 0.hour

        val (GHA, delta) = solarEphemeris(JD, UT)

        assertNearEquals(
            expected = 161.619,
            actual = GHA.value,
            tolerance = 5e-3
        )
        assertNearEquals(
            expected = -7.785,
            actual = delta.value,
            tolerance = 5e-3
        )
    }
}
