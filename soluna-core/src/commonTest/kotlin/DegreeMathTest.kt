import com.russhwolf.math.TAU
import com.russhwolf.math.acos
import com.russhwolf.math.asDegrees
import com.russhwolf.math.asRadians
import com.russhwolf.math.asin
import com.russhwolf.math.atan
import com.russhwolf.math.atan2
import com.russhwolf.math.cos
import com.russhwolf.math.sin
import com.russhwolf.math.tan
import kotlin.math.sqrt
import kotlin.math.ulp
import kotlin.test.Test

class DegreeMathTest {
    @Test
    fun sinTest() = multipleAssert(
        -360.0 to 0.0,
        -330.0 to 0.5,
        -315.0 to 1 / sqrt(2.0),
        -300.0 to sqrt(3.0) / 2.0,
        -270.0 to 1.0,
        -240.0 to sqrt(3.0) / 2.0,
        -225.0 to 1 / sqrt(2.0),
        -210.0 to 0.5,
        -180.0 to 0.0,
        -150.0 to -0.5,
        -135.0 to -1 / sqrt(2.0),
        -120.0 to -sqrt(3.0) / 2.0,
        -90.0 to -1.0,
        -60.0 to -sqrt(3.0) / 2.0,
        -45.0 to -1 / sqrt(2.0),
        -30.0 to -0.5,
        0.0 to 0.0,
        30.0 to 0.5,
        45.0 to 1 / sqrt(2.0),
        60.0 to sqrt(3.0) / 2.0,
        90.0 to 1.0,
        120.0 to sqrt(3.0) / 2.0,
        135.0 to 1 / sqrt(2.0),
        150.0 to 0.5,
        180.0 to 0.0,
        210.0 to -0.5,
        225.0 to -1 / sqrt(2.0),
        240.0 to -sqrt(3.0) / 2.0,
        270.0 to -1.0,
        300.0 to -sqrt(3.0) / 2.0,
        315.0 to -1 / sqrt(2.0),
        330.0 to -0.5,
        360.0 to 0.0,
        tolerance = 2e1.ulp
    ) { sin(it.asDegrees()) }

    @Test
    fun cosTest() = multipleAssert(
        -360.0 to 1.0,
        -330.0 to sqrt(3.0) / 2.0,
        -315.0 to 1 / sqrt(2.0),
        -300.0 to 0.5,
        -270.0 to 0.0,
        -240.0 to -0.5,
        -225.0 to -1 / sqrt(2.0),
        -210.0 to -sqrt(3.0) / 2.0,
        -180.0 to -1.0,
        -150.0 to -sqrt(3.0) / 2.0,
        -135.0 to -1 / sqrt(2.0),
        -120.0 to -0.5,
        -90.0 to 0.0,
        -60.0 to 0.5,
        -45.0 to 1 / sqrt(2.0),
        -30.0 to sqrt(3.0) / 2.0,
        0.0 to 1.0,
        30.0 to sqrt(3.0) / 2.0,
        45.0 to 1 / sqrt(2.0),
        60.0 to 0.5,
        90.0 to 0.0,
        120.0 to -0.5,
        135.0 to -1 / sqrt(2.0),
        150.0 to -sqrt(3.0) / 2.0,
        180.0 to -1.0,
        210.0 to -sqrt(3.0) / 2.0,
        225.0 to -1 / sqrt(2.0),
        240.0 to -0.5,
        270.0 to 0.0,
        300.0 to 0.5,
        315.0 to 1 / sqrt(2.0),
        330.0 to sqrt(3.0) / 2.0,
        360.0 to 1.0,
        tolerance = 2e1.ulp
    ) { cos(it.asDegrees()) }

    @Test
    fun tanTest() = multipleAssert(
        -360.0 to 0.0,
        -330.0 to 1 / sqrt(3.0),
        -315.0 to 1.0,
        -300.0 to sqrt(3.0),
        -240.0 to -sqrt(3.0),
        -225.0 to -1.0,
        -210.0 to -1 / sqrt(3.0),
        -180.0 to 0.0,
        -150.0 to 1 / sqrt(3.0),
        -135.0 to 1.0,
        -120.0 to sqrt(3.0),
        -60.0 to -sqrt(3.0),
        -45.0 to -1.0,
        -30.0 to -1 / sqrt(3.0),
        0.0 to 0.0,
        30.0 to 1 / sqrt(3.0),
        45.0 to 1.0,
        60.0 to sqrt(3.0),
        120.0 to -sqrt(3.0),
        135.0 to -1.0,
        150.0 to -1 / sqrt(3.0),
        180.0 to 0.0,
        210.0 to 1 / sqrt(3.0),
        225.0 to 1.0,
        240.0 to sqrt(3.0),
        300.0 to -sqrt(3.0),
        315.0 to -1.0,
        330.0 to -1 / sqrt(3.0),
        360.0 to 0.0,
        tolerance = 2e3.ulp
    ) { tan(it.asDegrees()) }

    @Test
    fun asinTest() = multipleAssert(
        -1.0 to -90.0,
        -sqrt(3.0) / 2.0 to -60.0,
        -1 / sqrt(2.0) to -45.0,
        -0.5 to -30.0,
        0.0 to 0.0,
        0.5 to 30.0,
        1 / sqrt(2.0) to 45.0,
        sqrt(3.0) / 2.0 to 60.0,
        1.0 to 90.0,
        tolerance = 2e2.ulp
    ) { asin(it).value }

    @Test
    fun acosTest() = multipleAssert(
        1.0 to 0.0,
        sqrt(3.0) / 2.0 to 30.0,
        1 / sqrt(2.0) to 45.0,
        0.5 to 60.0,
        0.0 to 90.0,
        -0.5 to 120.0,
        -1 / sqrt(2.0) to 135.0,
        -sqrt(3.0) / 2.0 to 150.0,
        -1.0 to 180.0,
        tolerance = 2e2.ulp
    ) { acos(it).value }

    @Test
    fun atanTest() = multipleAssert(
        Double.NEGATIVE_INFINITY to -90.0,
        -sqrt(3.0) to -60.0,
        -1.0 to -45.0,
        -1 / sqrt(3.0) to -30.0,
        0.0 to 0.0,
        1 / sqrt(3.0) to 30.0,
        1.0 to 45.0,
        sqrt(3.0) to 60.0,
        Double.POSITIVE_INFINITY to 90.0,
        tolerance = 2e2.ulp
    ) { atan(it).value }

    @Test
    fun atan2Test() = multipleAssert2(
        -0.0 to -1.0 to -180.0,
        -1 / sqrt(3.0) to -1.0 to -150.0,
        -1.0 to -1.0 to -135.0,
        -sqrt(3.0) to -1.0 to -120.0,
        Double.NEGATIVE_INFINITY to 1.0 to -90.0,
        Double.NEGATIVE_INFINITY to -1.0 to -90.0,
        -sqrt(3.0) to 1.0 to -60.0,
        -1.0 to 1.0 to -45.0,
        -1 / sqrt(3.0) to 1.0 to -30.0,
        0.0 to 1.0 to 0.0,
        1 / sqrt(3.0) to 1.0 to 30.0,
        1.0 to 1.0 to 45.0,
        sqrt(3.0) to 1.0 to 60.0,
        Double.POSITIVE_INFINITY to 1.0 to 90.0,
        Double.POSITIVE_INFINITY to -1.0 to 90.0,
        sqrt(3.0) to -1.0 to 120.0,
        1.0 to -1.0 to 135.0,
        1 / sqrt(3.0) to -1.0 to 150.0,
        0.0 to -1.0 to 180.0,
        tolerance = 2e2.ulp
    ) { (y, x) -> atan2(y, x).value }

    @Test
    fun degreeValues() = multipleAssert(
        -2.0 to 358.0,
        0.0 to 0.0,
        358.0 to 358.0,
        362.0 to 2.0,
        722.0 to 2.0,
        tolerance = 2e0.ulp
    ) { it.asDegrees().coercedValue }

    @Test
    fun radianValues() = multipleAssert(
        -0.2 to TAU - 0.2,
        0.0 to 0.0,
        TAU - 0.2 to TAU - 0.2,
        TAU + 0.2 to 0.2,
        2 * TAU + 0.2 to 0.2,
        tolerance = 2e1.ulp
    ) { it.asRadians().coercedValue }
}

fun multipleAssert(vararg values: Pair<Double, Double>, tolerance: Double, operator: (Double) -> Double) =
    values.forEach { (a, b) -> assertNearEquals(expected = b, actual = operator(a), tolerance = tolerance) }

fun multipleAssert2(
    vararg values: Pair<Pair<Double, Double>, Double>,
    tolerance: Double,
    operator: (Pair<Double, Double>) -> Double
) =
    values.forEach { (a, b) -> assertNearEquals(expected = b, actual = operator(a), tolerance = tolerance) }
