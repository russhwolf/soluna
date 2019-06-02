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

internal inline class Degree(internal val value: Double) : Comparable<Degree> {
    internal inline val coercedValue get() = value.coerceInLoopingRange(max = MAX.value)

    override fun compareTo(other: Degree): Int = value.compareTo(other.value)

    companion object {
        internal val MAX = 360.deg
    }
}

internal inline class Radian(internal val value: Double) : Comparable<Radian> {
    internal inline val coercedValue get() = value.coerceInLoopingRange(max = MAX.value)

    override fun compareTo(other: Radian): Int = value.compareTo(other.value)

    companion object {
        internal val MAX = TAU.rad
    }
}

internal fun sin(degrees: Degree) = sin(degrees.toRadians().value)
internal fun cos(degrees: Degree) = cos(degrees.toRadians().value)
internal fun tan(degrees: Degree) = tan(degrees.toRadians().value)
internal fun asin(value: Double) = asin(value).rad.toDegrees()
internal fun acos(value: Double) = acos(value).rad.toDegrees()
internal fun atan(value: Double) = atan(value).rad.toDegrees()
internal fun atan2(y: Double, x: Double) = atan2(y, x).rad.toDegrees()

internal inline fun Radian.toDegrees() = Degree(value * DEG_PER_RAD)
internal inline fun Degree.toRadians() = Radian(value / DEG_PER_RAD)
internal inline val Double.deg get() = Degree(this)
internal inline val Double.rad get() = Radian(this)
internal inline val Int.deg get() = toDouble().deg
internal inline val Int.rad get() = toDouble().rad

internal tailrec fun Double.coerceInLoopingRange(min: Double = 0.0, max: Double): Double {
    val range = max - min
    if (range <= 0) throw IllegalArgumentException("max=$max must be larger than min=$min")
    return when {
        this < min -> (this + range).coerceInLoopingRange(min, max)
        this > max -> (this - range).coerceInLoopingRange(min, max)
        else -> this
    }
}

internal operator fun Degree.plus(other: Degree) = (value + other.value).deg
internal operator fun Degree.minus(other: Degree) = (value - other.value).deg
internal operator fun Degree.times(other: Double) = (value * other).deg
internal operator fun Degree.times(other: Int) = (value * other).deg
internal operator fun Double.times(other: Degree) = (this * other.value).deg
internal operator fun Int.times(other: Degree) = (this * other.value).deg
internal operator fun Degree.div(other: Double) = (value / other).deg
internal operator fun Degree.div(other: Degree) = (value / other.value)
internal operator fun Degree.unaryMinus() = (-value).deg
internal operator fun Degree.compareTo(other: Degree) = value.compareTo(other.value)
internal operator fun Degree.rangeTo(that: Degree) = ClosedDegreeRange(this, that)

internal operator fun Radian.plus(other: Radian) = (value + other.value).rad
internal operator fun Radian.minus(other: Radian) = (value - other.value).rad
internal operator fun Radian.times(other: Double) = (value * other).rad
internal operator fun Radian.times(other: Int) = (value * other).rad
internal operator fun Double.times(other: Radian) = (this * other.value).rad
internal operator fun Int.times(other: Radian) = (this * other.value).rad
internal operator fun Radian.div(other: Double) = (value / other).rad
internal operator fun Radian.div(other: Radian) = (value / other.value)
internal operator fun Radian.unaryMinus() = (-value).rad
internal operator fun Radian.compareTo(other: Radian) = value.compareTo(other.value)
internal operator fun Radian.rangeTo(that: Radian) = ClosedRadianRange(this, that)

internal class ClosedDegreeRange(
    override val start: Degree,
    override val endInclusive: Degree
) : ClosedFloatingPointRange<Degree> {
    override fun lessThanOrEquals(a: Degree, b: Degree): Boolean = a.value <= b.value
}

internal class ClosedRadianRange(
    override val start: Radian,
    override val endInclusive: Radian
) : ClosedFloatingPointRange<Radian> {
    override fun lessThanOrEquals(a: Radian, b: Radian): Boolean = a.value <= b.value
}
