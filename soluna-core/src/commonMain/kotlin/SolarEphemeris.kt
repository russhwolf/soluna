@file:Suppress("LocalVariableName")

package com.russhwolf.soluna

import com.russhwolf.math.Degree
import com.russhwolf.math.HourAngle
import com.russhwolf.math.abs
import com.russhwolf.math.acos
import com.russhwolf.math.asin
import com.russhwolf.math.atan2
import com.russhwolf.math.cos
import com.russhwolf.math.deg
import com.russhwolf.math.div
import com.russhwolf.math.hour
import com.russhwolf.math.minus
import com.russhwolf.math.plus
import com.russhwolf.math.sin
import com.russhwolf.math.tan
import com.russhwolf.math.times
import com.russhwolf.math.toDegrees
import com.russhwolf.math.toHourAngle
import com.russhwolf.math.unaryMinus
import kotlin.math.sqrt

fun sunTimes(
    year: Int,
    month: Int,
    day: Int,
    offset: Double, // Hours
    latitude: Double, // Degrees
    longitude: Double // Degrees
): Pair<Double, Double> {
    val JD = julianDayNumber(year, month, day)
    val h = -(50.0 / 60.0).deg
    val riseTime = timeAtAltitude(::solarEphemeris, latitude.deg, longitude.deg, JD, offset.hour, +1, h)
    val setTime = timeAtAltitude(::solarEphemeris, latitude.deg, longitude.deg, JD, offset.hour, -1, h)

    return riseTime.value to setTime.value
}

fun moonTimes(
    year: Int,
    month: Int,
    day: Int,
    offset: Double, // Hours
    latitude: Double, // Degrees
    longitude: Double // Degrees
): Pair<Double, Double> {
    val JD = julianDayNumber(year, month, day)

    val T = (JD + 0.5 + offset / 24 - 2_451_545.0) / 36_525.0
    val pi = 0.9508.deg +
            0.0518.deg * cos(135.0.deg + 477_198.87.deg * T) + 0.0095.deg * cos(259.3.deg - 413_335.36.deg * T) +
            0.0078.deg * cos(235.7.deg + 890_534.22.deg * T) + 0.0028.deg * cos(269.9.deg + 954_397.74.deg * T)
    val h = -(34.0 / 60.0).deg + 0.7275 * pi

    val lunarEphemerisAtLocation = { JD: Int, UT: HourAngle -> lunarEphemeris(JD, UT, latitude.deg, longitude.deg) }

    val riseTime = timeAtAltitude(lunarEphemerisAtLocation, latitude.deg, longitude.deg, JD, offset.hour, +1, h)
    val setTime = timeAtAltitude(lunarEphemerisAtLocation, latitude.deg, longitude.deg, JD, offset.hour, -1, h)

    return riseTime.value to setTime.value
}

internal fun solarEphemeris(
    JD: Int, // Julian date
    UT: HourAngle // Time, in hours
): Pair<Degree, Degree> {
    // Based on Section 12.3.1.1 (p. 513)

    // Centuries since J2000.0 (eq. 12.6)
    val T = (JD + 0.5 + UT / HourAngle.MAX - 2_451_545.0) / 36_525.0

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

    // Greenwich hour angle (in degrees)
    val GHA = UT.toDegrees() - 180.deg + E

    // Declination
    val delta = asin(sin(epsilon) * sin(lambda))

    return GHA to delta
}

internal fun lunarEphemeris(
    JD: Int,
    UT: HourAngle,
    latitude: Degree,
    longitude: Degree
): Pair<Degree, Degree> {
    // Centuries since J2000.0 (eq. 12.6)
    val T = (JD + UT / HourAngle.MAX - 2_451_545.0) / 36_525.0

    // Remaining formula from page D22 of Astronomical Almanac 2019
    val lambda = 218.32.deg + 481_267.881.deg * T +
            6.29.deg * sin(135.0.deg + 477_198.87.deg * T) - 1.27.deg * sin(259.3.deg - 413_335.36.deg * T) +
            0.66.deg * sin(235.7.deg + 890_534.22.deg * T) + 0.21.deg * sin(269.9.deg + 954_397.74.deg * T) -
            0.19.deg * sin(357.5.deg + 35_999.05.deg * T) - 0.11.deg * sin(186.5.deg + 966_404.03.deg * T)

    val beta = 0.deg +
            5.13.deg * sin(93.3.deg + 483_202.02.deg * T) + 0.28.deg * sin(228.2.deg + 960_400.89.deg * T) -
            0.28.deg * sin(318.3.deg + 6_003.15.deg * T) - 0.17.deg * sin(217.6.deg - 407_332.21.deg * T)

    val pi = 0.9508.deg +
            0.0518.deg * cos(135.0.deg + 477_198.87.deg * T) + 0.0095.deg * cos(259.3.deg - 413_335.36.deg * T) +
            0.0078.deg * cos(235.7.deg + 890_534.22.deg * T) + 0.0028.deg * cos(269.9.deg + 954_397.74.deg * T)

    val l = cos(beta) * cos(lambda)
    val m = 0.9175 * cos(beta) * sin(lambda) - 0.3978 * sin(beta)
    val n = 0.3978 * cos(beta) * sin(lambda) + 0.9171 * sin(beta)

    val alpha = atan2(m, l)
    val delta = asin(n)

    val r = 1 / sin(pi)

    val x = r * l
    val y = r * m
    val z = r * n

    val phi_prime = latitude
    val lambda_prime = longitude

    val T_nu = (JD - 2451545.0) / 36_525
    val theta_0 = 100.46.deg + 36_000.77.deg * T_nu + lambda_prime + UT.toDegrees()

    val x_prime = x - cos(phi_prime) * cos(theta_0)
    val y_prime = y - cos(phi_prime) * sin(theta_0)
    val z_prime = z - sin(phi_prime)

    val r_prime = sqrt(x_prime * x_prime + y_prime * y_prime + z_prime * z_prime)
    val alpha_prime = atan2(y_prime, x_prime)
    val delta_prime = asin(z_prime / r_prime)

    return alpha_prime to delta_prime
}

internal fun timeAtAltitude(
    ephemeris: (Int, HourAngle) -> Pair<Degree, Degree>,
    phi: Degree, // Latitude
    lambda: Degree, // Longitude, NOT lambda FROM solarEphemeris()
    JD: Int,
    offset: HourAngle,
    sign: Int, // +1 for rise, -1 for set
    h: Degree // Altitude to solve for
): HourAngle {
    // Based on section 12.3.3 (p. 515)
    var UT0 = 12.hour - offset

    var diff = Double.MAX_VALUE.hour
    for (i in 1..100) {
        if (abs(diff) < 0.008.hour) break

        val (GHA, delta) = ephemeris(JD, UT0)

        // Hour angle (eq 12.11 and 12.12)
        val cos_t = (sin(h) - sin(phi) * sin(delta)) / (cos(phi) * cos(delta))
        val t = when {
            cos_t > 1 -> 0.deg
            cos_t < -1 -> 180.deg
            else -> acos(cos_t)
        }

        // Update guess (eq. 12.10)
        diff = -(GHA + lambda + sign * t).toHourAngle()
        UT0 += diff
    }

    // TODO add correction described in 12.13 for high latitudes

    UT0 = (UT0 + offset)
    while (UT0 < 0.hour) UT0 += 24.hour
    while (UT0 > 24.hour) UT0 -= 24.hour
    return UT0
}

// Refraction (eq. 12.14, p. 516)
private fun R(h_alpha: Degree) = 0.0167.deg / tan(h_alpha + 7.31.deg / (h_alpha.value + 4.4))
