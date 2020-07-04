package com.russhwolf.soluna.time

import com.russhwolf.soluna.MoonPhase
import io.islandtime.Date
import io.islandtime.Month
import io.islandtime.Time
import io.islandtime.TimeZone
import io.islandtime.asTimeZone
import io.islandtime.asUtcOffset
import io.islandtime.measures.Duration
import io.islandtime.measures.abs
import io.islandtime.measures.asDuration
import io.islandtime.measures.durationOf
import io.islandtime.measures.hours
import io.islandtime.measures.minutes
import io.islandtime.measures.seconds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class TimesTest {
    @Test
    fun sunTimesTest() {
        val year = 2019
        val month = Month.OCTOBER

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
        ).map { pair ->
            pair.toList()
                .map { Time(it.substring(0, 2).toInt(), it.substring(2, 4).toInt()) }
                .let { it[0] to it[1] }
        }

        (1..31).map {
            sunTimes(
                date = Date(year, month, it),
                zone = (-5).hours.asUtcOffset().asTimeZone(),
                latitude = 42.383,
                longitude = -71.117
            )
        }.forEachIndexed { index, it ->
            it.toList().zip(expected[index].toList()).forEach { (actual, expected) ->
                assertNearEquals(expected, actual?.time ?: Time(0, 0), 60.seconds.asDuration())
            }
        }
    }

    @Test
    fun moonTimesTest() {
        val year = 2019
        val month = Month.OCTOBER

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
        ).map { pair ->
            pair.toList()
                .map { it?.let { Time(it.substring(0, 2).toInt(), it.substring(2, 4).toInt()) } }
                .let { it[0] to it[1] }
        }

        (1..31).map {
            moonTimes(
                date = Date(year, month, it),
                zone = (-5).hours.asUtcOffset().asTimeZone(),
                latitude = 42.383,
                longitude = -71.117
            )
        }.forEachIndexed { index, it ->
            it.toList().zip(expected[index].toList()).forEach { (actual, expected) ->
                assertNearEquals(
                    expected ?: Time(0, 0),
                    actual?.time ?: Time(0, 0),
                    (5.minutes + 30.seconds).asDuration()
                )
            }
        }
    }

    @Test
    fun moonPhaseTest() {
        // Test case adapted from Astronomical Algorithms, Example 49.a (p. 353)
        assertEquals(MoonPhase.NEW, moonPhase(Date(1977, 2, 18), TimeZone.UTC, 0.0))
    }
}

fun assertNearEquals(
    expected: Time,
    actual: Time,
    tolerance: Duration,
    message: String = "expected:<$expected±$tolerance> but was:<$actual>"
) {
    val difference = durationOf(expected.nanosecondsSinceStartOfDay - actual.nanosecondsSinceStartOfDay)
    assertTrue(abs(difference) <= tolerance, message)
}
