@file:OptIn(ExperimentalTime::class)

package com.russhwolf.soluna.android.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.times
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.russhwolf.soluna.android.R
import com.russhwolf.soluna.android.extensions.toDisplayTime
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@Composable
fun SunMoonTimesGraphic(
    currentTime: Instant,
    sunriseTime: Instant?,
    sunsetTime: Instant?,
    moonriseTime: Instant?,
    moonsetTime: Instant?,
    timeZone: TimeZone,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color(0x80808080)
    val sunColor = SolunaTheme.colors.primary
    val moonColor = SolunaTheme.colors.secondary
    val currentTimeColor = SolunaTheme.colors.onBackground
    val midnightColor = Color(0x80808080)
    val textColor = SolunaTheme.colors.onBackground
    val largeTextStyle = SolunaTheme.typography.body1
    val smallTextStyle = SolunaTheme.typography.body2
    val smallPaint = remember { Paint() }
    val largePaint = remember { Paint() }
    val midnightPath = remember { Path() }
    val noonPath = remember { Path() }
    val currentTimePath = remember { Path() }

    BoxWithConstraints(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        val minSize = minOf(maxHeight, maxWidth)
        val dpPerSp = LocalContext.current.resources.displayMetrics.run { scaledDensity / density }
        // TODO make this value less hacky
        val outerPadding = (1.25 * (largeTextStyle.fontSize.value + smallTextStyle.fontSize.value) * dpPerSp).dp
        val timesArcThickness = 32.dp
        val timesArcMargin = 8.dp
        val diskRadius = minSize / 2 - outerPadding
        val moonTimesRadius = diskRadius - timesArcMargin - timesArcThickness / 2
        val sunTimesRadius = diskRadius - 2 * timesArcMargin - 3 * timesArcThickness / 2
        val moonTimesOffset = outerPadding + timesArcMargin + 1 / 2f * timesArcThickness
        val sunTimesOffset = outerPadding + 2 * timesArcMargin + 3 / 2f * timesArcThickness
        val currentTimeWidth = 4.dp
        val midnightWidth = 4.dp
        val midnightString = stringResource(R.string.sunmoontimes_midnight)
        val noonString = stringResource(R.string.sunmoontimes_noon)

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f, matchHeightConstraintsFirst = maxHeight < maxWidth)
        ) {
            drawCircle(
                color = backgroundColor,
                radius = diskRadius.toPx()
            )
            drawLine(
                color = midnightColor,
                start = Offset(size.width / 2, outerPadding.toPx()),
                end = Offset(size.width / 2, size.height / 2),
                strokeWidth = midnightWidth.toPx(),
                cap = StrokeCap.Round
            )

            rotate(-90f) {
                drawTimesArc(
                    currentTime = currentTime,
                    riseTime = sunriseTime,
                    setTime = sunsetTime,
                    timeZone = timeZone,
                    color = sunColor,
                    thickness = timesArcThickness,
                    radius = sunTimesRadius,
                    arcOffset = sunTimesOffset
                )
                drawTimesArc(
                    currentTime = currentTime,
                    riseTime = moonriseTime,
                    setTime = moonsetTime,
                    timeZone = timeZone,
                    color = moonColor,
                    thickness = timesArcThickness,
                    radius = moonTimesRadius,
                    arcOffset = moonTimesOffset
                )
            }
            rotate(currentTime.toAngle(timeZone)) {
                drawLine(
                    color = currentTimeColor,
                    start = Offset(size.width / 2, size.height / 2),
                    end = Offset(size.width / 2, outerPadding.toPx()),
                    strokeWidth = currentTimeWidth.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            drawIntoCanvas {
                val canvas = it.nativeCanvas
                smallPaint.apply {
                    color = textColor.toArgb()
                    textSize = smallTextStyle.fontSize.toPx()
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                largePaint.apply {
                    color = textColor.toArgb()
                    textSize = largeTextStyle.fontSize.toPx()
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                midnightPath.apply {
                    reset()
                    arcTo(
                        RectF(
                            0f,
                            outerPadding.toPx() - smallPaint.fontMetrics.descent,
                            size.width,
                            size.height - outerPadding.toPx() + smallPaint.fontMetrics.descent
                        ), -180f, 180f, true
                    )
                }
                canvas.drawTextOnPath(midnightString, midnightPath, 0f, 0f, smallPaint)

                noonPath.apply {
                    reset()
                    arcTo(
                        RectF(
                            0f,
                            outerPadding.toPx() + smallPaint.fontMetrics.ascent,
                            size.width,
                            size.height - outerPadding.toPx() - smallPaint.fontMetrics.ascent
                        ), 180f, -180f, true
                    )
                }
                canvas.drawTextOnPath(noonString, noonPath, 0f, 0f, smallPaint)

                currentTimePath.apply {
                    reset()
                    val angle = currentTime.toAngle(timeZone)
                    if (currentTime.toLocalDateTime(timeZone).hour in 6 until 18) {
                        val offset =
                            outerPadding.toPx() + largePaint.fontMetrics.ascent + smallPaint.fontMetrics.run { top - bottom }
                        arcTo(
                            RectF(offset, offset, size.width - offset, size.height - offset),
                            angle,
                            -180f,
                            true
                        )
                    } else {
                        val offset =
                            outerPadding.toPx() - largePaint.fontMetrics.descent + smallPaint.fontMetrics.run { top - bottom }
                        arcTo(
                            RectF(offset, offset, size.width - offset, size.height - offset),
                            angle - 180f,
                            180f,
                            true
                        )
                    }
                }
                canvas.drawTextOnPath(currentTime.toDisplayTime(timeZone), currentTimePath, 0f, 0f, largePaint)
            }
        }

        TimeIcon(
            angle = sunriseTime?.toAngle(timeZone),
            radius = sunTimesRadius,
            size = timesArcThickness,
            iconColor = SolunaTheme.colors.onPrimary,
            icon = Icons.Filled.LightMode
        )
        TimeIcon(
            angle = sunsetTime?.toAngle(timeZone),
            radius = sunTimesRadius,
            size = timesArcThickness,
            iconColor = SolunaTheme.colors.onPrimary,
            icon = Icons.Outlined.LightMode
        )
        TimeIcon(
            angle = moonriseTime?.toAngle(timeZone),
            radius = moonTimesRadius,
            size = timesArcThickness,
            iconColor = SolunaTheme.colors.onSecondary,
            icon = Icons.Filled.DarkMode
        )
        TimeIcon(
            angle = moonsetTime?.toAngle(timeZone),
            radius = moonTimesRadius,
            size = timesArcThickness,
            iconColor = SolunaTheme.colors.onSecondary,
            icon = Icons.Outlined.DarkMode
        )
    }
}

private fun DrawScope.drawTimesArc(
    currentTime: Instant,
    riseTime: Instant?,
    setTime: Instant?,
    timeZone: TimeZone,
    color: Color,
    thickness: Dp,
    radius: Dp,
    arcOffset: Dp
) {
    if (riseTime != null || setTime != null) {
        val effectiveRiseTime = riseTime ?: currentTime
        val effectiveSetTime = setTime ?: currentTime.plus(1.days)

        val flip = riseTime != null && setTime != null &&
                riseTime.toAngle(timeZone) > setTime.toAngle(timeZone)

        drawArc(
            color = color,
            startAngle = effectiveRiseTime.toAngle(timeZone),
            sweepAngle = (effectiveSetTime - effectiveRiseTime).toAngle() / 2,
            useCenter = false,
            style = Stroke(
                thickness.toPx(),
                cap = if (riseTime != null) StrokeCap.Round else StrokeCap.Butt
            ),
            size = 2 * Size(radius.toPx(), radius.toPx()),
            topLeft = Offset(arcOffset.toPx(), arcOffset.toPx())
        )
        drawArc(
            color = color,
            startAngle = (effectiveRiseTime.toAngle(timeZone) + effectiveSetTime.toAngle(timeZone)) / 2
                    + (if (flip) -180 else 0),
            sweepAngle = (effectiveSetTime - effectiveRiseTime).toAngle() / 2,
            useCenter = false,
            style = Stroke(
                thickness.toPx(),
                cap = if (setTime != null) StrokeCap.Round else StrokeCap.Butt
            ),
            size = 2 * Size(radius.toPx(), radius.toPx()),
            topLeft = Offset(arcOffset.toPx(), arcOffset.toPx())
        )
    }
}

@Composable
private fun TimeIcon(angle: Float?, radius: Dp, size: Dp, iconColor: Color, icon: ImageVector) {
    if (angle != null) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .rotate(180 + angle)
                .offset(0.dp, radius)
                .rotate(-(180 + angle)),
            tint = iconColor
        )
    }
}

private fun Duration.toAngle(): Float = let {
    if (it < 0.days) it + 1.days else it
}.toDouble(DurationUnit.DAYS).toFloat() * 360

private fun Instant.toAngle(timeZone: TimeZone): Float {
    val localDateTime = toLocalDateTime(timeZone)
    val localMidnightInstant = localDateTime.run { LocalDateTime(year, month, dayOfMonth, 0, 0) }.toInstant(timeZone)
    return (this - localMidnightInstant).toAngle()
}

@Suppress("unused")
class Previews {
    private val defaultTimeZone = TimeZone.UTC

    private val defaultCurrentTime = LocalDateTime(2021, 1, 1, 11, 0).toInstant(defaultTimeZone)

    private val defaultSunriseTime = LocalDateTime(2021, 1, 2, 7, 30).toInstant(defaultTimeZone)
    private val defaultSunsetTime = LocalDateTime(2021, 1, 1, 18, 0).toInstant(defaultTimeZone)

    private val defaultMoonriseTime = LocalDateTime(2021, 1, 2, 9, 0).toInstant(defaultTimeZone)
    private val defaultMoonsetTime = LocalDateTime(2021, 1, 1, 20, 0).toInstant(defaultTimeZone)

    @Preview(showBackground = true)
    @Composable
    fun Standard_Light() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = defaultMoonriseTime,
                    moonsetTime = defaultMoonsetTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
    @Composable
    fun Standard_Dark() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = defaultMoonriseTime,
                    moonsetTime = defaultMoonsetTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview(fontScale = 2f, showBackground = true)
    @Composable
    fun LargeText() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = defaultMoonriseTime,
                    moonsetTime = defaultMoonsetTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview(heightDp = 320, widthDp = 320, showBackground = true)
    @Composable
    fun SmallBox() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = defaultMoonriseTime,
                    moonsetTime = defaultMoonsetTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview
    @Composable
    fun Later() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime.plus(12.hours),
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = defaultMoonriseTime,
                    moonsetTime = defaultMoonsetTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview
    @Composable
    fun InvertedMoon() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = defaultMoonsetTime,
                    moonsetTime = defaultMoonriseTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview
    @Composable
    fun NoSunrise() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = null,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = defaultMoonriseTime,
                    moonsetTime = defaultMoonsetTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview
    @Composable
    fun NoSunset() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = null,
                    moonriseTime = defaultMoonriseTime,
                    moonsetTime = defaultMoonsetTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview
    @Composable
    fun NoSunEvents() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = null,
                    sunsetTime = null,
                    moonriseTime = defaultMoonriseTime,
                    moonsetTime = defaultMoonsetTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview
    @Composable
    fun NoMoonRise() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = null,
                    moonsetTime = defaultMoonsetTime,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview
    @Composable
    fun NoMoonSet() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = defaultMoonriseTime,
                    moonsetTime = null,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview
    @Composable
    fun NoMoonEvents() {
        SolunaTheme {
            Box(Modifier.aspectRatio(1f)) {
                SunMoonTimesGraphic(
                    currentTime = defaultCurrentTime,
                    sunriseTime = defaultSunriseTime,
                    sunsetTime = defaultSunsetTime,
                    moonriseTime = null,
                    moonsetTime = null,
                    timeZone = defaultTimeZone
                )
            }
        }
    }

    @Preview
    @Composable
    fun Interactive() {
        fun onValueChange(timeState: MutableState<Instant>, currentTimeState: MutableState<Instant>): (Float) -> Unit =
            { value ->
                val localMidnight = currentTimeState.value.toLocalDateTime(defaultTimeZone)
                    .run { LocalDateTime(year, month, dayOfMonth, 0, 0) }
                val instantAtValue = localMidnight.toInstant(defaultTimeZone) + (value / 360.0).days
                timeState.value = instantAtValue +
                        if (currentTimeState.value > instantAtValue) 1.days else Duration.ZERO
            }

        val hasSunrise = remember { mutableStateOf(true) }
        val hasSunset = remember { mutableStateOf(true) }
        val hasMoonrise = remember { mutableStateOf(true) }
        val hasMoonset = remember { mutableStateOf(true) }
        val currentTime = remember { mutableStateOf(defaultCurrentTime) }
        val sunriseTime = remember { mutableStateOf(defaultSunriseTime) }
        val sunsetTime = remember { mutableStateOf(defaultSunsetTime) }
        val moonriseTime = remember { mutableStateOf(defaultMoonriseTime) }
        val moonsetTime = remember { mutableStateOf(defaultMoonsetTime) }

        SolunaTheme {
            Column {
                Text("Current Time: ${currentTime.value}")
                Slider(
                    value = currentTime.value.toAngle(defaultTimeZone),
                    onValueChange = { value ->
                        val localMidnight = currentTime.value.toLocalDateTime(defaultTimeZone)
                            .run { LocalDateTime(year, month, dayOfMonth, 0, 0) }
                        val instantAtValue = localMidnight.toInstant(defaultTimeZone) + (value / 360.0).days
                        currentTime.value = instantAtValue
                    },
                    valueRange = 0f..360f
                )
                Text("Sunrise Time: ${sunriseTime.value}")
                Row {
                    Checkbox(checked = hasSunrise.value, onCheckedChange = { hasSunrise.value = it })
                    Slider(
                        value = sunriseTime.value.toAngle(defaultTimeZone),
                        onValueChange = onValueChange(sunriseTime, currentTime),
                        valueRange = 0f..360f
                    )
                }
                Text("Sunset Time: ${sunsetTime.value}")
                Row {
                    Checkbox(checked = hasSunset.value, onCheckedChange = { hasSunset.value = it })
                    Slider(
                        value = sunsetTime.value.toAngle(defaultTimeZone),
                        onValueChange = onValueChange(sunsetTime, currentTime),
                        valueRange = 0f..360f
                    )
                }
                Text("Moonrise Time: ${moonriseTime.value}")
                Row {
                    Checkbox(checked = hasMoonrise.value, onCheckedChange = { hasMoonrise.value = it })
                    Slider(
                        value = moonriseTime.value.toAngle(defaultTimeZone),
                        onValueChange = onValueChange(moonriseTime, currentTime),
                        valueRange = 0f..360f
                    )
                }
                Text("Moonset Time: ${moonsetTime.value}")
                Row {
                    Checkbox(checked = hasMoonset.value, onCheckedChange = { hasMoonset.value = it })
                    Slider(
                        value = moonsetTime.value.toAngle(defaultTimeZone),
                        onValueChange = onValueChange(moonsetTime, currentTime),
                        valueRange = 0f..360f
                    )
                }

                Box(Modifier.aspectRatio(1f)) {
                    SunMoonTimesGraphic(
                        currentTime = currentTime.value,
                        sunriseTime = sunriseTime.value.takeIf { hasSunrise.value },
                        sunsetTime = sunsetTime.value.takeIf { hasSunset.value },
                        moonriseTime = moonriseTime.value.takeIf { hasMoonrise.value },
                        moonsetTime = moonsetTime.value.takeIf { hasMoonset.value },
                        timeZone = defaultTimeZone
                    )
                }
            }
        }
    }
}
