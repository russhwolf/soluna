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

internal interface AngleUnit<T : AngleUnit<T>> : Comparable<T> {
    val value: Double
    val coercedValue: Double

    override fun compareTo(other: T): Int = value.compareTo(other.value)
}

internal inline class Degree(override val value: Double) : AngleUnit<Degree> {
    override val coercedValue: Double get() = value.coerceInLoopingRange(max = MAX.value)

    companion object {
        internal val MAX = 360.deg
    }
}

internal inline class Radian(override val value: Double) : AngleUnit<Radian> {
    override val coercedValue: Double get() = value.coerceInLoopingRange(max = MAX.value)

    companion object {
        internal val MAX = TAU.rad
    }
}

private tailrec fun Double.coerceInLoopingRange(min: Double = 0.0, max: Double): Double {
    val range = max - min
    if (range <= 0) throw IllegalArgumentException("max=$max must be larger than min=$min")
    return when {
        this < min -> (this + range).coerceInLoopingRange(min, max)
        this > max -> (this - range).coerceInLoopingRange(min, max)
        else -> this
    }
}

internal fun sin(degrees: Degree) = sin(degrees.toRadians().value)
internal fun cos(degrees: Degree) = cos(degrees.toRadians().value)
internal fun tan(degrees: Degree) = tan(degrees.toRadians().value)
internal fun asin(value: Double) = asin(value).rad.toDegrees()
internal fun acos(value: Double) = acos(value).rad.toDegrees()
internal fun atan(value: Double) = atan(value).rad.toDegrees()
internal fun atan2(y: Double, x: Double) = atan2(y, x).rad.toDegrees()

internal inline fun Radian.toDegrees() = (value * DEG_PER_RAD).deg
internal inline fun Degree.toRadians() = (value / DEG_PER_RAD).rad
internal inline val Double.deg get() = Degree(this)
internal inline val Double.rad get() = Radian(this)
internal inline val Int.deg get() = toDouble().deg

private inline fun <reified T : AngleUnit<T>> Double.asUnit(): T = when (T::class) {
    Degree::class -> this.deg
    Radian::class -> this.rad
    else -> throw IllegalArgumentException()
} as T

internal inline operator fun <reified T : AngleUnit<T>> T.plus(other: T): T = (value + other.value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.minus(other: T): T = (value - other.value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.times(other: Double): T = (value * other).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.times(other: Int): T = (value * other).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> Double.times(other: T): T = (this * other.value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> Int.times(other: T): T = (this * other.value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.div(other: Double): T = (value / other).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.div(other: T): Double = (value / other.value)
internal inline operator fun <reified T : AngleUnit<T>> T.unaryMinus(): T = (-value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.rangeTo(that: T) = ClosedAngleUnitRange(this, that)

internal class ClosedAngleUnitRange<T : AngleUnit<T>>(
    override val start: T,
    override val endInclusive: T
) : ClosedFloatingPointRange<T> {
    override fun lessThanOrEquals(a: T, b: T): Boolean = a.value <= b.value
}
