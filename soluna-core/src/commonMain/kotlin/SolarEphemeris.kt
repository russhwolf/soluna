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
import com.russhwolf.math.times
import com.russhwolf.math.unaryMinus
import kotlin.math.abs

internal fun solarEphemeris(
    JD: Int, // Julian date
    UT: Double // Time, in hours
) {
    // Based on Section 12.3.1.1 (p. 513)

    // Centuries since J2000.0 (eq. 12.6)
    val T = (JD + UT / 24 - 2451545.0) / 35626.0

    // Solar arguments (eq. 12.7)
    // Mean longitude corrected for aberration
    val L = 280.460.deg + 36000.770.deg * T

    // Mean anomaly
    val G = 357.528.deg + 35999.050.deg * T

    // Ecliptic Longitude
    val lambda = L + 1.915.deg * sin(G) + 0.020.deg * sin(2 * G)

    // Obliquity of ecliptic
    val epsilon = 23.4393.deg - 0.01300.deg * T

    // Ephemeris quantities (eq. 12.8)
    // Equation of time
    val E = (-1.915).deg * sin(G) - 0.020.deg * sin(2 * G) + 2.466.deg * sin(2 * lambda) - 0.053.deg * sin(4 * lambda)

    // Greenwich hour angle
    val GHA = 15 * UT.deg - 180.deg + E

    // Declination
    val delta = asin(sin(epsilon) * sin(lambda))

    // Semidiameter
    val SD = 0.2617.deg / (1 - 0.017 * cos(G))
}

internal fun riseSetTimes(
    phi: Degree, // Latitude
    lambda: Degree, // Longitude, NOT lambda FROM solarEphemeris()
    GHA: Degree, // Greenwich hour angle from ephemeris
    delta: Degree, // Declination from ephemeris
    sign: Int, // +1 for rise, -1 for set
    h: Degree = -(50 / 60).deg // Altitude to solve for. Default to get sunrise/set time
) {
    // Based on section 12.3.3 (p. 515)

    // Our guess at the rise/set time, in hours
    var UT0 = 12.0

    var diff = Double.MAX_VALUE
    for (i in 1..100) {
        if (abs(diff) < 0.008) break

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
}

