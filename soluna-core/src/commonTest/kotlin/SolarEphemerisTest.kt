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
import kotlin.math.roundToLong
import kotlin.test.Test

// TODO better testing needed here
class SolarEphemerisTest {
    @Test
    fun sunTimesTest() {
        // Expected data from https://aa.usno.navy.mil/data/docs/RS_OneYear.php
        val expected = listOf(
            "0541" to "1726",
            "0542" to "1725",
            "0543" to "1723",
            "0545" to "1721",
            "0546" to "1719",
            "0547" to "1718",
            "0548" to "1716",
            "0549" to "1714",
            "0550" to "1713",
            "0551" to "1711",
            "0552" to "1709",
            "0554" to "1708",
            "0555" to "1706",
            "0556" to "1704",
            "0557" to "1703",
            "0558" to "1701",
            "0559" to "1700",
            "0601" to "1658",
            "0602" to "1657",
            "0603" to "1655",
            "0604" to "1653",
            "0605" to "1652",
            "0606" to "1650",
            "0608" to "1649",
            "0609" to "1648",
            "0610" to "1646",
            "0611" to "1645",
            "0613" to "1643",
            "0614" to "1642",
            "0615" to "1641",
            "0616" to "1639"
        ).mapIndexed { index, pair ->
            pair.toList()
                .map { it.substring(0, 2).toDouble() + it.substring(2, 4).toDouble() / 60 }
                .map {
                    (((julianDayNumber(2019, 10, index + 1) - julianDayNumber(
                        1970,
                        1,
                        1
                    )) * 24 + it + 5) * 60 * 60 * 1000).roundToLong()
                }
                .let { it[0] to it[1] }
        }

        (1..31).map { day ->
            sunTimes(
                year = 2019,
                month = 10,
                day = day,
                offset = -5.0,
                latitude = 42.383,
                longitude = -71.117
            )
        }.forEachIndexed { index, it ->
            it.toList().zip(expected[index].toList()).forEach {
                assertNearEquals(it.second, it.first ?: 0, tolerance = 60 * 1000)
            }
        }
    }

    @Test
    fun moonTimesTest() {
        // Expected data from https://aa.usno.navy.mil/data/docs/RS_OneYear.php
        val expected = listOf(
            "0858" to "1934",
            "1009" to "2011",
            "1117" to "2054",
            "1220" to "2141",
            "1315" to "2233",
            "1403" to "2329",
            "1443" to null,
            "1518" to "0027",
            "1549" to "0127",
            "1615" to "0226",
            "1640" to "0325",
            "1704" to "0424",
            "1728" to "0524",
            "1753" to "0624",
            "1820" to "0726",
            "1850" to "0828",
            "1926" to "0932",
            "2008" to "1035",
            "2058" to "1136",
            "2156" to "1233",
            "2302" to "1323",
            null to "1408",
            "0013" to "1447",
            "0127" to "1521",
            "0243" to "1553",
            "0359" to "1623",
            "0515" to "1654",
            "0631" to "1727",
            "0746" to "1803",
            "0858" to "1844",
            "1005" to "1930"
        ).mapIndexed { index, it ->
            it.toList()
                .map { it?.let { it.substring(0, 2).toDouble() + it.substring(2, 4).toDouble() / 60 } }
                .map {
                    it?.let {
                        (((julianDayNumber(2019, 10, index + 1) - julianDayNumber(
                            1970,
                            1,
                            1
                        )) * 24 + it + 5) * 60 * 60 * 1000).roundToLong()
                    }
                }
                .let { it[0] to it[1] }
        }

        (1..31).map { day ->
            moonTimes(
                year = 2019,
                month = 10,
                day = day,
                offset = -5.0,
                latitude = 42.383,
                longitude = -71.117
            )
        }.forEachIndexed { index, it ->
            it.toList().zip(expected[index].toList()).forEach {
                assertNearEquals(it.second ?: 0, it.first ?: 0, tolerance = 6 * 60 * 1000)
            }
        }
    }

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
}
