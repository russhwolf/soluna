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
    val D = day - 1 // Algorithm assumes 0-indexed day (apparently)

    val (y, j, m, n, r, p, q, v, u, s, t, w) = calendar

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
