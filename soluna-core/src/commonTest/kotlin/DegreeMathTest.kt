package com.russhwolf.math

import com.russhwolf.test.expects
import com.russhwolf.test.multipleAssert
import kotlin.math.sqrt
import kotlin.math.ulp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DegreeMathTest {
    @Test
    fun sinTest() = multipleAssert(
        -360.0 expects 0.0,
        -330.0 expects 0.5,
        -315.0 expects 1 / sqrt(2.0),
        -300.0 expects sqrt(3.0) / 2.0,
        -270.0 expects 1.0,
        -240.0 expects sqrt(3.0) / 2.0,
        -225.0 expects 1 / sqrt(2.0),
        -210.0 expects 0.5,
        -180.0 expects 0.0,
        -150.0 expects -0.5,
        -135.0 expects -1 / sqrt(2.0),
        -120.0 expects -sqrt(3.0) / 2.0,
        -90.0 expects -1.0,
        -60.0 expects -sqrt(3.0) / 2.0,
        -45.0 expects -1 / sqrt(2.0),
        -30.0 expects -0.5,
        0.0 expects 0.0,
        30.0 expects 0.5,
        45.0 expects 1 / sqrt(2.0),
        60.0 expects sqrt(3.0) / 2.0,
        90.0 expects 1.0,
        120.0 expects sqrt(3.0) / 2.0,
        135.0 expects 1 / sqrt(2.0),
        150.0 expects 0.5,
        180.0 expects 0.0,
        210.0 expects -0.5,
        225.0 expects -1 / sqrt(2.0),
        240.0 expects -sqrt(3.0) / 2.0,
        270.0 expects -1.0,
        300.0 expects -sqrt(3.0) / 2.0,
        315.0 expects -1 / sqrt(2.0),
        330.0 expects -0.5,
        360.0 expects 0.0,
        tolerance = 2e1.ulp
    ) { sin(it.deg) }

    @Test
    fun cosTest() = multipleAssert(
        -360.0 expects 1.0,
        -330.0 expects sqrt(3.0) / 2.0,
        -315.0 expects 1 / sqrt(2.0),
        -300.0 expects 0.5,
        -270.0 expects 0.0,
        -240.0 expects -0.5,
        -225.0 expects -1 / sqrt(2.0),
        -210.0 expects -sqrt(3.0) / 2.0,
        -180.0 expects -1.0,
        -150.0 expects -sqrt(3.0) / 2.0,
        -135.0 expects -1 / sqrt(2.0),
        -120.0 expects -0.5,
        -90.0 expects 0.0,
        -60.0 expects 0.5,
        -45.0 expects 1 / sqrt(2.0),
        -30.0 expects sqrt(3.0) / 2.0,
        0.0 expects 1.0,
        30.0 expects sqrt(3.0) / 2.0,
        45.0 expects 1 / sqrt(2.0),
        60.0 expects 0.5,
        90.0 expects 0.0,
        120.0 expects -0.5,
        135.0 expects -1 / sqrt(2.0),
        150.0 expects -sqrt(3.0) / 2.0,
        180.0 expects -1.0,
        210.0 expects -sqrt(3.0) / 2.0,
        225.0 expects -1 / sqrt(2.0),
        240.0 expects -0.5,
        270.0 expects 0.0,
        300.0 expects 0.5,
        315.0 expects 1 / sqrt(2.0),
        330.0 expects sqrt(3.0) / 2.0,
        360.0 expects 1.0,
        tolerance = 2e1.ulp
    ) { cos(it.deg) }

    @Test
    fun tanTest() = multipleAssert(
        -360.0 expects 0.0,
        -330.0 expects 1 / sqrt(3.0),
        -315.0 expects 1.0,
        -300.0 expects sqrt(3.0),
        -240.0 expects -sqrt(3.0),
        -225.0 expects -1.0,
        -210.0 expects -1 / sqrt(3.0),
        -180.0 expects 0.0,
        -150.0 expects 1 / sqrt(3.0),
        -135.0 expects 1.0,
        -120.0 expects sqrt(3.0),
        -60.0 expects -sqrt(3.0),
        -45.0 expects -1.0,
        -30.0 expects -1 / sqrt(3.0),
        0.0 expects 0.0,
        30.0 expects 1 / sqrt(3.0),
        45.0 expects 1.0,
        60.0 expects sqrt(3.0),
        120.0 expects -sqrt(3.0),
        135.0 expects -1.0,
        150.0 expects -1 / sqrt(3.0),
        180.0 expects 0.0,
        210.0 expects 1 / sqrt(3.0),
        225.0 expects 1.0,
        240.0 expects sqrt(3.0),
        300.0 expects -sqrt(3.0),
        315.0 expects -1.0,
        330.0 expects -1 / sqrt(3.0),
        360.0 expects 0.0,
        tolerance = 2e3.ulp
    ) { tan(it.deg) }

    @Test
    fun asinTest() = multipleAssert(
        -1.0 expects -90.0,
        -sqrt(3.0) / 2.0 expects -60.0,
        -1 / sqrt(2.0) expects -45.0,
        -0.5 expects -30.0,
        0.0 expects 0.0,
        0.5 expects 30.0,
        1 / sqrt(2.0) expects 45.0,
        sqrt(3.0) / 2.0 expects 60.0,
        1.0 expects 90.0,
        tolerance = 2e2.ulp
    ) { asin(it).value }

    @Test
    fun acosTest() = multipleAssert(
        1.0 expects 0.0,
        sqrt(3.0) / 2.0 expects 30.0,
        1 / sqrt(2.0) expects 45.0,
        0.5 expects 60.0,
        0.0 expects 90.0,
        -0.5 expects 120.0,
        -1 / sqrt(2.0) expects 135.0,
        -sqrt(3.0) / 2.0 expects 150.0,
        -1.0 expects 180.0,
        tolerance = 2e2.ulp
    ) { acos(it).value }

    @Test
    fun atanTest() = multipleAssert(
        Double.NEGATIVE_INFINITY expects -90.0,
        -sqrt(3.0) expects -60.0,
        -1.0 expects -45.0,
        -1 / sqrt(3.0) expects -30.0,
        0.0 expects 0.0,
        1 / sqrt(3.0) expects 30.0,
        1.0 expects 45.0,
        sqrt(3.0) expects 60.0,
        Double.POSITIVE_INFINITY expects 90.0,
        tolerance = 2e2.ulp
    ) { atan(it).value }

    @Test
    fun atan2Test() = multipleAssert(
        -0.0 to -1.0 expects -180.0,
        -1 / sqrt(3.0) to -1.0 expects -150.0,
        -1.0 to -1.0 expects -135.0,
        -sqrt(3.0) to -1.0 expects -120.0,
        Double.NEGATIVE_INFINITY to 1.0 expects -90.0,
        Double.NEGATIVE_INFINITY to -1.0 expects -90.0,
        -sqrt(3.0) to 1.0 expects -60.0,
        -1.0 to 1.0 expects -45.0,
        -1 / sqrt(3.0) to 1.0 expects -30.0,
        0.0 to 1.0 expects 0.0,
        1 / sqrt(3.0) to 1.0 expects 30.0,
        1.0 to 1.0 expects 45.0,
        sqrt(3.0) to 1.0 expects 60.0,
        Double.POSITIVE_INFINITY to 1.0 expects 90.0,
        Double.POSITIVE_INFINITY to -1.0 expects 90.0,
        sqrt(3.0) to -1.0 expects 120.0,
        1.0 to -1.0 expects 135.0,
        1 / sqrt(3.0) to -1.0 expects 150.0,
        0.0 to -1.0 expects 180.0,
        tolerance = 2e2.ulp
    ) { (y, x) -> atan2(y, x).value }

    @Test
    fun degreeValues() = multipleAssert(
        -2.0 expects 358.0,
        0.0 expects 0.0,
        358.0 expects 358.0,
        362.0 expects 2.0,
        722.0 expects 2.0,
        tolerance = 2e0.ulp
    ) { it.deg.coercedValue }

    @Test
    fun radianValues() = multipleAssert(
        -0.2 expects TAU - 0.2,
        0.0 expects 0.0,
        TAU - 0.2 expects TAU - 0.2,
        TAU + 0.2 expects 0.2,
        2 * TAU + 0.2 expects 0.2,
        tolerance = 2e1.ulp
    ) { it.rad.coercedValue }

    @Test
    fun angleUnitPlus() {
        assertEquals(3.deg, 1.deg + 2.deg)
        assertEquals(3.rad, 1.rad + 2.rad)
        assertEquals(3.hour, 1.hour + 2.hour)
    }

    @Test
    fun angleUnitMinus() {
        assertEquals(1.deg, 3.deg - 2.deg)
        assertEquals(1.rad, 3.rad - 2.rad)
        assertEquals(1.hour, 3.hour - 2.hour)
    }

    @Test
    fun angleUnitTimes() {
        assertEquals(4.deg, 2 * 2.deg)
        assertEquals(4.rad, 2 * 2.rad)
        assertEquals(4.hour, 2 * 2.hour)
        assertEquals(4.deg, 2.deg * 2)
        assertEquals(4.rad, 2.rad * 2)
        assertEquals(4.hour, 2.hour * 2)
    }

    @Test
    fun angleUnitDiv() {
        assertEquals(2.deg, 4.deg / 2)
        assertEquals(2.rad, 4.rad / 2)
        assertEquals(2.hour, 4.hour / 2)
        assertEquals(2.0, 4.deg / 2.deg)
        assertEquals(2.0, 4.rad / 2.rad)
        assertEquals(2.0, 4.hour / 2.hour)
    }

    @Test
    fun angleUnitUnaryMinus() {
        assertEquals((-2).deg, -(2.deg))
        assertEquals((-2).rad, -(2.rad))
        assertEquals((-2).hour, -(2.hour))
    }

    @Test
    fun angleUnitRange() {
        assertTrue(1.deg in 0.deg..2.deg)
        assertTrue(3.deg !in 0.deg..2.deg)
        assertTrue((-1).deg !in 0.deg..2.deg)
        assertTrue(1.rad in 0.rad..2.rad)
        assertTrue(3.rad !in 0.rad..2.rad)
        assertTrue((-1).rad !in 0.rad..2.rad)
        assertTrue(1.hour in 0.hour..2.hour)
        assertTrue(3.hour !in 0.hour..2.hour)
        assertTrue((-1).hour !in 0.hour..2.hour)
    }

    @Test
    fun angleUnitAbs() {
        assertEquals(1.deg, abs((-1).deg))
        assertEquals(1.deg, abs(1.deg))
        assertEquals(1.rad, abs((-1).rad))
        assertEquals(1.rad, abs(1.rad))
        assertEquals(1.hour, abs((-1).hour))
        assertEquals(1.hour, abs(1.hour))
    }
}
