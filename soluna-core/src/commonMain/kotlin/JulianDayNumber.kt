// Using names to match references (either Explanatory Supplement to the Astronomical Almanac, or others noted inline),
// so don't warn on naming
@file:Suppress("LocalVariableName", "PropertyName", "UnnecessaryVariable")

package com.russhwolf.soluna

internal fun julianDayNumber(
    year: Int,
    month: Int,
    day: Int,
    calendar: Calendar = GregorianCalendar
): Int {
    // Section 15.11.3, Algorithm 3 (p. 618)
    val Y = year
    val M = month
    val D = day

    val (y, j, m, n, r, p, q, _, u, s, t, _) = calendar

    val h = M - m
    val g = Y + y - (n - h) / n
    val f = (h - 1 + n) % n
    val e = (p * g + q) / r + D - 1 - j

    val J = e + (s * f + t) / u +
            if (calendar is LeapDayCalendar) {
                val A = calendar.A
                val C = calendar.C
                -(3 * ((g + A) / 100)) / 4 - C
            } else 0

    return J
}

internal fun dateFromJulianDayNumber(
    JD: Int,
    calendar: Calendar = GregorianCalendar
): Triple<Int, Int, Int> {
    // Section 15.11.4, Algorithm 4 (p. 619)
    val J = JD
    val (y, j, m, n, r, p, _, v, u, s, _, w) = calendar

    val f = J + j + if (calendar is LeapDayCalendar) {
        val B = calendar.B
        val C = calendar.C
        (((4 * J + B) / 146_097) * 3) / 4 + C
    } else 0
    val e = r * f + v
    val g = e % p / r
    val h = u * g + w
    val D = h % s / u + 1
    val M = (h / s + m) % n + 1
    val Y = e / p - y + (n + m - M) / n

    return Triple(Y, M, D)
}

// Based on table 15.14 (p. 617)
internal interface Calendar {
    val y: Int
    val j: Int
    val m: Int
    val n: Int
    val r: Int
    val p: Int
    val q: Int
    val v: Int
    val u: Int
    val s: Int
    val t: Int
    val w: Int

    operator fun component1() = y
    operator fun component2() = j
    operator fun component3() = m
    operator fun component4() = n
    operator fun component5() = r
    operator fun component6() = p
    operator fun component7() = q
    operator fun component8() = v
    operator fun component9() = u
    operator fun component10() = s
    operator fun component11() = t
    operator fun component12() = w
}

internal interface LeapDayCalendar : Calendar {
    val A: Int
    val B: Int
    val C: Int

    operator fun component13() = A
    operator fun component14() = B
    operator fun component15() = C
}

internal object JulianCalendar : Calendar {
    override val y: Int = 4716
    override val j: Int = 1401
    override val m: Int = 2
    override val n: Int = 12
    override val r: Int = 4
    override val p: Int = 1461
    override val q: Int = 0
    override val v: Int = 3
    override val u: Int = 5
    override val s: Int = 153
    override val t: Int = 2
    override val w: Int = 2
}

internal object GregorianCalendar : Calendar by JulianCalendar, LeapDayCalendar {
    override val A: Int = 184
    override val B: Int = 274227
    override val C: Int = -38
}
