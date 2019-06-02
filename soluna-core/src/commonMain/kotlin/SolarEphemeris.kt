@file:Suppress("LocalVariableName")

package com.russhwolf.soluna

import com.russhwolf.math.Degree
import com.russhwolf.math.acos
import com.russhwolf.math.asin
import com.russhwolf.math.cos
import com.russhwolf.math.deg
import com.russhwolf.math.div
import com.russhwolf.math.minus
import com.russhwolf.math.plus
import com.russhwolf.math.sin
import com.russhwolf.math.tan
import com.russhwolf.math.times
import com.russhwolf.math.unaryMinus
import kotlin.math.abs

fun sunTimes(
    year: Int,
    month: Int,
    day: Int,
    offset: Double, // Hours
    latitude: Double, // Degrees
    longitude: Double // Degrees
): Pair<Double, Double> {
    val JD = julianDayNumber(year, month, day)
    val riseTime = timeAtAltitude(::solarEphemeris, latitude.deg, longitude.deg, JD, offset, +1)
    val setTime = timeAtAltitude(::solarEphemeris, latitude.deg, longitude.deg, JD, offset, -1)

    return riseTime to setTime
}

internal fun solarEphemeris(
    JD: Int, // Julian date
    UT: Double // Time, in hours
): Pair<Degree, Degree> {
    // Based on Section 12.3.1.1 (p. 513)

    // Centuries since J2000.0 (eq. 12.6)
    val T = (JD + UT / 24 - 2_451_545.0) / 36_525.0

    // Solar arguments (eq. 12.7)
    // Mean longitude corrected for aberration
    val L = 280.460.deg + 36_000.770.deg * T

    // Mean anomaly
    val G = 357.528.deg + 35_999.050.deg * T

    // Ecliptic Longitude
    val lambda = L + 1.915.deg * sin(G) + 0.020.deg * sin(2 * G)

    // Obliquity of ecliptic
    val epsilon = 23.4393.deg - 0.01300.deg * T

    // Ephemeris quantities (eq. 12.8)
    // Equation of time
    val E = (-1.915).deg * sin(G) - 0.020.deg * sin(2 * G) + 2.466.deg * sin(2 * lambda) - 0.053.deg * sin(4 * lambda)

    // Greenwich hour angle
    val GHA = 15.deg * UT - 180.deg + E

    // Declination
    val delta = asin(sin(epsilon) * sin(lambda))

    return GHA to delta
}

internal fun timeAtAltitude(
    ephemeris: (Int, Double) -> Pair<Degree, Degree>,
    phi: Degree, // Latitude
    lambda: Degree, // Longitude, NOT lambda FROM solarEphemeris()
    JD: Int,
    offset: Double,
    sign: Int, // +1 for rise, -1 for set
    h: Degree = -(50.0 / 60.0).deg // Altitude to solve for. Default to get sunrise/set time
): Double {
    // Based on section 12.3.3 (p. 515)

    var UT0 = 12.0 - offset

    var diff = Double.MAX_VALUE
    for (i in 1..100) {
        if (abs(diff) < 0.008) break

        val (GHA, delta) = ephemeris(JD, UT0)

        // Hour angle (eq 12.11 and 12.12)
        val cos_t = (sin(h) - sin(phi) * sin(delta)) / (cos(phi) * cos(delta))
        val t = when {
            cos_t > 1 -> 0.deg
            cos_t < -1 -> 180.deg
            else -> acos(cos_t)
        }

        // Update guess (eq. 12.10)
        diff = -(GHA + lambda + sign * t).value / 15.0
        UT0 += diff
    }

    // TODO add correction described in 12.13 for high latitudes

    UT0 += offset
    while (UT0 < 0) UT0 += 24
    while (UT0 > 24) UT0 -= 24
    return UT0
}

// Refraction (eq. 12.14, p. 516)
private fun R(h_alpha: Degree) = 0.0167.deg / tan(h_alpha + 7.31.deg / (h_alpha.value + 4.4))
