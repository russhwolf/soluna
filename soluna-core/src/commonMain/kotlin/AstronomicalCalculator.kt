// Using names to match references (either Explanatory Supplement to the Astronomical Almanac, or others noted inline),
// so don't warn on naming
@file:Suppress(
    "LocalVariableName",
    "PropertyName",
    "PrivatePropertyName",
    "UnnecessaryVariable",
    "FloatingPointLiteralPrecision"
)

package com.russhwolf.soluna

import com.russhwolf.soluna.math.Degree
import com.russhwolf.soluna.math.HourAngle
import com.russhwolf.soluna.math.TAU
import com.russhwolf.soluna.math.abs
import com.russhwolf.soluna.math.acos
import com.russhwolf.soluna.math.asin
import com.russhwolf.soluna.math.atan2
import com.russhwolf.soluna.math.cos
import com.russhwolf.soluna.math.deg
import com.russhwolf.soluna.math.div
import com.russhwolf.soluna.math.hour
import com.russhwolf.soluna.math.minus
import com.russhwolf.soluna.math.plus
import com.russhwolf.soluna.math.rad
import com.russhwolf.soluna.math.sin
import com.russhwolf.soluna.math.times
import com.russhwolf.soluna.math.toDegrees
import com.russhwolf.soluna.math.toHourAngle
import com.russhwolf.soluna.math.unaryMinus
import kotlin.math.roundToLong
import kotlin.math.sqrt

interface AstronomicalCalculator<out TimeUnit : Any> {
    val sunTimes: RiseSetResult<TimeUnit>
    val moonTimes: RiseSetResult<TimeUnit>
    val moonPhase: MoonPhase?
}

fun <A : Any, B : Any> AstronomicalCalculator<A>.map(transform: (A) -> B): AstronomicalCalculator<B> =
    object : AstronomicalCalculator<B> {
        override val sunTimes: RiseSetResult<B> by lazy { this@map.sunTimes.map(transform) }
        override val moonTimes: RiseSetResult<B> by lazy { this@map.moonTimes.map(transform) }
        override val moonPhase: MoonPhase? by lazy { this@map.moonPhase }
    }

class MillisTimeAstronomicalCalculator(
    year: Int,
    month: Int,
    day: Int,
    offsetHours: Double, // Hours
    latitudeDegrees: Double, // Degrees
    longitudeDegrees: Double // Degrees
) : AstronomicalCalculator<Long> {
    private val JD by lazy {
        julianDayNumber(year, month, day)
    }

    private val lunarEphemerisAtLocation: (Int, HourAngle) -> EphemerisPoint by lazy {
        { JD: Int, UT: HourAngle -> lunarEphemeris(JD, UT, latitudeDegrees.deg, longitudeDegrees.deg) }
    }

    private val sunriseTime: EventResult<Long> by lazy {
        timeAtAltitude(
            ::solarEphemeris,
            ::solarSize,
            latitudeDegrees.deg,
            longitudeDegrees.deg,
            JD,
            offsetHours.hour,
            +1
        ).map { it.toMillisTime(JD, offsetHours) }
    }

    private val sunsetTime: EventResult<Long> by lazy {
        timeAtAltitude(
            ::solarEphemeris,
            ::solarSize,
            latitudeDegrees.deg,
            longitudeDegrees.deg,
            JD,
            offsetHours.hour,
            -1
        ).map { it.toMillisTime(JD, offsetHours) }
    }

    private val moonriseTime: EventResult<Long> by lazy {
        timeAtAltitude(
            lunarEphemerisAtLocation,
            ::lunarSize,
            latitudeDegrees.deg,
            longitudeDegrees.deg,
            JD,
            offsetHours.hour,
            +1
        ).map { it.toMillisTime(JD, offsetHours) }
    }

    private val moonsetTime: EventResult<Long> by lazy {
        timeAtAltitude(
            lunarEphemerisAtLocation,
            ::lunarSize,
            latitudeDegrees.deg,
            longitudeDegrees.deg,
            JD,
            offsetHours.hour,
            -1
        ).map { it.toMillisTime(JD, offsetHours) }
    }

    override val sunTimes by lazy { combineResults(sunriseTime, sunsetTime) }

    override val moonTimes by lazy { combineResults(moonriseTime, moonsetTime) }

    override val moonPhase: MoonPhase? by lazy {
        moonPhase(longitudeDegrees.deg, latitudeDegrees.deg, JD, offsetHours.hour)
    }
}

private fun HourAngle.toMillisTime(JD: Int, offset: Double): Long {
    val hoursSinceEpoch = (JD - julianDayNumber(1970, 1, 1)) * 24
    val millisTime = (hoursSinceEpoch + this.value - offset) * 60 * 60 * 1000
    return millisTime.roundToLong()
}

internal data class EphemerisPoint(val GHA: Degree, val delta: Degree, val pi: Degree = 0.deg)

@Suppress("UNUSED_PARAMETER") // Time calculation needs Degree input, but sun doesn't use it
private fun solarSize(pi: Degree) = (50.0 / 60.0).deg

private fun lunarSize(pi: Degree) = (34.0 / 60.0).deg + 0.7275 * pi

internal fun solarEphemeris(
    JD: Int, // Julian date
    UT: HourAngle // Time, in hours
): EphemerisPoint {
    // Based on Section 12.3.1.1 (p. 513)

    // Centuries since J2000.0 (eq. 12.6)
    val T = (JD - 0.5 + UT / HourAngle.MAX - 2_451_545.0) / 36_525.0

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

    return EphemerisPoint(GHA, delta)
}

internal fun lunarEphemeris(
    JD: Int,
    UT: HourAngle,
    latitude: Degree,
    longitude: Degree
): EphemerisPoint {
    // Centuries since J2000.0 (eq. 12.6)
    val T = (JD - 0.5 + UT / HourAngle.MAX - 2_451_545.0) / 36_525.0

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

    val r = 1 / sin(pi)
    val x = r * l
    val y = r * m
    val z = r * n

    val phi_prime = latitude

    val lambda_prime = longitude

    val T_nu = (JD - 0.5 - 2451545.0) / 36_525
    val theta_0 = 100.46.deg + 36_000.77.deg * T_nu + lambda_prime + UT.toDegrees()

    val x_prime = x - cos(phi_prime) * cos(theta_0)
    val y_prime = y - cos(phi_prime) * sin(theta_0)
    val z_prime = z - sin(phi_prime)

    val r_prime = sqrt(x_prime * x_prime + y_prime * y_prime + z_prime * z_prime)
    val alpha_prime = atan2(y_prime, x_prime)
    val delta_prime = asin(z_prime / r_prime)
    val pi_prime = asin(1 / r_prime)

    // Earth Rotation Angle (eq 3.3, p. 78)
    val t_u = (JD - 0.5 + UT / HourAngle.MAX - 2_451_545.0)
    val Theta = TAU * (0.779_057_273_264_0 + 1.002_737_811_911_354_48 * t_u).rad

    // Hour Angle via Earth Rotation Angle and Right Ascension (is this right?) (eq. 3.15, p. 80)
    val GHA = (Theta.toDegrees() - alpha_prime).coerceInRange()

    return EphemerisPoint(GHA, delta_prime, pi_prime)
}

private fun timeAtAltitude(
    ephemeris: (Int, HourAngle) -> EphemerisPoint,
    size: (Degree) -> Degree,
    phi: Degree, // Latitude
    lambda: Degree, // Longitude, NOT lambda from solarEphemeris()
    JD: Int,
    offset: HourAngle,
    sign: Int // +1 for rise, -1 for set
): EventResult<HourAngle> {
    // Based on section 12.3.3 (p. 515)
    var UT = (-12).hour + offset

    var converged = false
    var cos_t = 0.0
    for (i in 1..100) {
        val UT0 = UT

        val (GHA, delta, pi) = ephemeris(JD, UT0)
        val h = -size(pi)

        // Hour angle (eq 12.11 and 12.12)
        cos_t = (sin(h) - sin(phi) * sin(delta)) / (cos(phi) * cos(delta))
        val t = when {
            cos_t > 1 -> 0.deg
            cos_t < -1 -> 180.deg
            else -> acos(cos_t)
        }

        // Update guess (eq. 12.10)
        UT = (UT0 + -(GHA + lambda + sign * t).toHourAngle() + offset).coerceInRange() - offset
        if (abs(UT0 - UT) < 0.008.hour) {
            converged = true
            break
        }
    }

    // TODO add correction described in 12.13 for high latitudes

    return when {
        !converged -> EventResult.Error
        cos_t > 1 -> EventResult.TooLow // Likely down all day
        cos_t < -1 -> EventResult.TooHigh // Likely up all day
        else -> EventResult.Value((UT + offset).coerceInRange())
    }
}

private fun moonPhase(
    latitude: Degree,
    longitude: Degree,
    JD: Int,
    offset: HourAngle
): MoonPhase? {
    val UTstart = 0.hour + offset
    val UTend = 24.hour + offset

    val phaseEnd = moonPhaseTable(JD, UTend, latitude, longitude)
    val phaseStart = moonPhaseTable(JD, UTstart, latitude, longitude)
        .let { if (it > phaseEnd) it - Degree.MAX else it }

    for (phase in MoonPhase.entries) {
        if (phase.angle in phaseStart..phaseEnd) {
            return phase
        }
    }
    return null
}

private fun moonPhaseTable(JD: Int, UT: HourAngle, latitude: Degree, longitude: Degree): Degree {
    val solarGHA = solarEphemeris(JD, UT).GHA
    val lunarGHA = lunarEphemeris(JD, UT, latitude, longitude).GHA
    return (solarGHA - lunarGHA).coerceInRange()
}
