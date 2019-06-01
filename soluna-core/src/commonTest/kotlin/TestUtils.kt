import kotlin.math.abs
import kotlin.math.ulp
import kotlin.test.assertTrue

fun assertNearEquals(
    expected: Double,
    actual: Double,
    tolerance: Double = expected.ulp,
    message: String = "expected:<$expected±$tolerance> but was:<$actual>"
) = assertTrue(abs(expected - actual) <= tolerance, message)
