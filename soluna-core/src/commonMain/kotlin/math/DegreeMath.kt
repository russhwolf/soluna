@file:Suppress("NOTHING_TO_INLINE")

package com.russhwolf.soluna.math

internal const val TAU: Double = 2 * kotlin.math.PI
private val DEG_PER_RAD: Double = Degree.MAX.value / Radian.MAX.value
private val DEG_PER_HOUR: Double = Degree.MAX.value / HourAngle.MAX.value

internal interface AngleUnit<T : AngleUnit<T>> : Comparable<T> {
    val value: Double
    fun coerceInRange(): T

    override fun compareTo(other: T): Int = value.compareTo(other.value)
}

internal inline class Degree(override val value: Double) : AngleUnit<Degree> {
    override fun coerceInRange(): Degree = value.coerceInLoopingRange(max = MAX.value).deg

    companion object {
        internal val MAX = 360.deg
    }
}

internal inline class Radian(override val value: Double) : AngleUnit<Radian> {
    override fun coerceInRange(): Radian = value.coerceInLoopingRange(max = MAX.value).rad

    companion object {
        internal val MAX = TAU.rad
    }
}

internal inline class HourAngle(override val value: Double) : AngleUnit<HourAngle> {
    override fun coerceInRange(): HourAngle = value.coerceInLoopingRange(max = MAX.value).hour

    companion object {
        internal val MAX = 24.hour
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

internal fun sin(degrees: Degree) = kotlin.math.sin(degrees.toRadians().value)
internal fun cos(degrees: Degree) = kotlin.math.cos(degrees.toRadians().value)
internal fun tan(degrees: Degree) = kotlin.math.tan(degrees.toRadians().value)
internal fun asin(value: Double) = kotlin.math.asin(value).rad.toDegrees()
internal fun acos(value: Double) = kotlin.math.acos(value).rad.toDegrees()
internal fun atan(value: Double) = kotlin.math.atan(value).rad.toDegrees()
internal fun atan2(y: Double, x: Double) = kotlin.math.atan2(y, x).rad.toDegrees()

internal inline fun Radian.toDegrees() = (value * DEG_PER_RAD).deg
internal inline fun Degree.toRadians() = (value / DEG_PER_RAD).rad
internal inline fun HourAngle.toDegrees() = (value * DEG_PER_HOUR).deg
internal inline fun Degree.toHourAngle() = (value / DEG_PER_HOUR).hour
internal inline val Double.deg get() = Degree(this)
internal inline val Double.rad get() = Radian(this)
internal inline val Double.hour get() = HourAngle(this)
internal inline val Int.deg get() = toDouble().deg
internal inline val Int.rad get() = toDouble().rad
internal inline val Int.hour get() = toDouble().hour

private inline fun <reified T : AngleUnit<T>> Double.asUnit(): T = when (T::class) {
    Degree::class -> this.deg
    Radian::class -> this.rad
    HourAngle::class -> this.hour
    else -> throw IllegalArgumentException("Invalid unit ${T::class}")
} as T

internal inline operator fun <reified T : AngleUnit<T>> T.plus(other: T): T = (value + other.value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.minus(other: T): T = (value - other.value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.times(other: Double): T = (value * other).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.times(other: Int): T = (value * other).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> Double.times(other: T): T = (this * other.value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> Int.times(other: T): T = (this * other.value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.div(other: Double): T = (value / other).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.div(other: Int): T = (value / other.toDouble()).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.div(other: T): Double = (value / other.value)
internal inline operator fun <reified T : AngleUnit<T>> T.unaryMinus(): T = (-value).asUnit()
internal inline operator fun <reified T : AngleUnit<T>> T.rangeTo(that: T) = ClosedAngleUnitRange(this, that)

internal inline fun <reified T : AngleUnit<T>> abs(x: T): T = kotlin.math.abs(x.value).asUnit()

internal class ClosedAngleUnitRange<T : AngleUnit<T>>(
    override val start: T,
    override val endInclusive: T
) : ClosedFloatingPointRange<T> {
    override fun lessThanOrEquals(a: T, b: T): Boolean = a.value <= b.value
}
