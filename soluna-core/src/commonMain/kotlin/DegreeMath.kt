@file:Suppress("NOTHING_TO_INLINE")

package com.russhwolf.math

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

internal const val TAU: Double = 2 * PI
private val DEG_PER_RAD: Double = Degree.MAX.value / Radian.MAX.value

internal inline class Degree(internal val value: Double) {
    companion object {
        internal val MAX = 360.0.asDegrees()
    }

    internal inline val coercedValue get() = value.coerceInLoopingRange(max = MAX.value)
}

internal inline class Radian(internal val value: Double) {
    companion object {
        internal val MAX = TAU.asRadians()
    }

    internal inline val coercedValue get() = value.coerceInLoopingRange(max = MAX.value)
}

internal fun sin(degrees: Degree) = sin(degrees.toRadians().value)
internal fun cos(degrees: Degree) = cos(degrees.toRadians().value)
internal fun tan(degrees: Degree) = tan(degrees.toRadians().value)
internal fun asin(value: Double) = asin(value).asRadians().toDegrees()
internal fun acos(value: Double) = acos(value).asRadians().toDegrees()
internal fun atan(value: Double) = atan(value).asRadians().toDegrees()
internal fun atan2(y: Double, x: Double) = atan2(y, x).asRadians().toDegrees()

internal inline fun Radian.toDegrees() = Degree(value * DEG_PER_RAD)
internal inline fun Degree.toRadians() = Radian(value / DEG_PER_RAD)
internal inline fun Double.asDegrees() = Degree(this)
internal inline fun Double.asRadians() = Radian(this)

internal tailrec fun Double.coerceInLoopingRange(min: Double = 0.0, max: Double): Double {
    val range = max - min
    if (range <= 0) throw IllegalArgumentException("max=$max must be larger than min=$min")
    return when {
        this < min -> (this + range).coerceInLoopingRange(min, max)
        this > max -> (this - range).coerceInLoopingRange(min, max)
        else -> this
    }
}

internal operator fun Degree.plus(other: Degree) = (value + other.value).asDegrees()
internal operator fun Degree.minus(other: Degree) = (value - other.value).asDegrees()
internal operator fun Degree.times(other: Degree) = (value * other.value).asDegrees()
internal operator fun Degree.div(other: Degree) = (value / other.value).asDegrees()
internal operator fun Degree.compareTo(other: Degree) = value.compareTo(other.value)
internal operator fun Degree.rangeTo(that: Degree) = value.rangeTo(that.value)

internal operator fun Radian.plus(other: Radian) = (value + other.value).asRadians()
internal operator fun Radian.minus(other: Radian) = (value - other.value).asRadians()
internal operator fun Radian.times(other: Radian) = (value * other.value).asRadians()
internal operator fun Radian.div(other: Radian) = (value / other.value).asRadians()
internal operator fun Radian.compareTo(other: Radian) = value.compareTo(other.value)
internal operator fun Radian.rangeTo(that: Radian) = value.rangeTo(that.value)
