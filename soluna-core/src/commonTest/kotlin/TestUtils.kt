package com.russhwolf.test

import kotlin.math.abs
import kotlin.math.ulp
import kotlin.test.assertTrue

fun assertNearEquals(
    expected: Double,
    actual: Double,
    tolerance: Double = expected.ulp,
    message: String = "expected:<$expected±$tolerance> but was:<$actual>"
) = assertTrue(abs(expected - actual) <= tolerance, message)

fun <T> multipleAssert(vararg values: MultipleAssert<T, Double>, tolerance: Double, operator: (T) -> Double) =
    values.forEach { (input, expected) ->
        val actual = operator(input)
        assertNearEquals(
            expected = expected,
            actual = actual,
            tolerance = tolerance,
            message = "expected:<$expected±$tolerance> but was:<$actual> for input:<$input>"
        )
    }

data class MultipleAssert<A, B>(val input: A, val expected: B)

infix fun <A, B> A.expects(that: B) = MultipleAssert(this, that)
