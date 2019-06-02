package com.russhwolf.soluna

import kotlin.math.floor
import kotlin.math.roundToInt
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
    }

    private fun Double.printTime() {
        val hours = floor(this).toInt()
        val minutes = ((this - hours) * 60).roundToInt()

        println("$hours:$minutes")
    }
}
