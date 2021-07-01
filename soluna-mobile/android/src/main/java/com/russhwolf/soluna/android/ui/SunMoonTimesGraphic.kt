@file:OptIn(ExperimentalTime::class)

package com.russhwolf.soluna.android.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.times
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.russhwolf.soluna.android.extensions.toDisplayTime
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
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
    val effectiveSunriseTime = sunriseTime ?: currentTime
    val effectiveSunsetTime = sunsetTime ?: currentTime.plus(Duration.days(1))
    val effectiveMoonriseTime = moonriseTime ?: currentTime
    val effectiveMoonsetTime = moonsetTime ?: currentTime.plus(Duration.days(1))

    val backgroundColor = Color(0x80808080)
    val sunColor = SolunaTheme.colors.primary
    val moonColor = SolunaTheme.colors.secondary
    val currentTimeColor = SolunaTheme.colors.onBackground
    val midnightColor = Color(0x80808080)
    val textColor = SolunaTheme.colors.onBackground
    val largeTextStyle = SolunaTheme.typography.body1
    val smallTextStyle = SolunaTheme.typography.body2

    BoxWithConstraints(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        val minSize = minOf(maxHeight, maxWidth)
        val outerPadding = 48.dp // TODO increase based on text size
        val timesArcThickness = 32.dp
        val timesArcMargin = 8.dp
        val diskRadius = minSize / 2 - outerPadding
        val moonTimesRadius = diskRadius - timesArcMargin - timesArcThickness / 2
        val sunTimesRadius = diskRadius - 2 * timesArcMargin - 3 * timesArcThickness / 2
        val moonTimesOffset = outerPadding + timesArcMargin + 1 / 2f * timesArcThickness
        val sunTimesOffset = outerPadding + 2 * timesArcMargin + 3 / 2f * timesArcThickness
        val currentTimeWidth = 4.dp
        val midnightWidth = 4.dp

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
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
                if (sunriseTime != null || sunsetTime != null) {
                    drawArc(
                        color = sunColor,
                        startAngle = effectiveSunriseTime.toAngle(timeZone),
                        sweepAngle = (effectiveSunsetTime - effectiveSunriseTime).toAngle() / 2,
                        useCenter = false,
                        style = Stroke(
                            timesArcThickness.toPx(),
                            cap = if (sunriseTime != null) StrokeCap.Round else StrokeCap.Butt
                        ),
                        size = 2 * Size(sunTimesRadius.toPx(), sunTimesRadius.toPx()),
                        topLeft = Offset(sunTimesOffset.toPx(), sunTimesOffset.toPx())
                    )
                    drawArc(
                        color = sunColor,
                        startAngle = (effectiveSunriseTime.toAngle(timeZone) + effectiveSunsetTime.toAngle(timeZone)) / 2,
                        sweepAngle = (effectiveSunsetTime - effectiveSunriseTime).toAngle() / 2,
                        useCenter = false,
                        style = Stroke(
                            timesArcThickness.toPx(),
                            cap = if (sunsetTime != null) StrokeCap.Round else StrokeCap.Butt
                        ),
                        size = 2 * Size(sunTimesRadius.toPx(), sunTimesRadius.toPx()),
                        topLeft = Offset(sunTimesOffset.toPx(), sunTimesOffset.toPx())
                    )
                }
                if (moonriseTime != null || moonsetTime != null) {
                    drawArc(
                        color = moonColor,
                        startAngle = effectiveMoonriseTime.toAngle(timeZone),
                        sweepAngle = (effectiveMoonsetTime - effectiveMoonriseTime).toAngle() / 2,
                        useCenter = false,
                        style = Stroke(
                            timesArcThickness.toPx(),
                            cap = if (moonriseTime != null) StrokeCap.Round else StrokeCap.Butt
                        ),
                        size = 2 * Size(moonTimesRadius.toPx(), moonTimesRadius.toPx()),
                        topLeft = Offset(moonTimesOffset.toPx(), moonTimesOffset.toPx())
                    )
                    drawArc(
                        color = moonColor,
                        startAngle = (effectiveMoonriseTime.toAngle(timeZone) + effectiveMoonsetTime.toAngle(timeZone)) / 2,
                        sweepAngle = (effectiveMoonsetTime - effectiveMoonriseTime).toAngle() / 2,
                        useCenter = false,
                        style = Stroke(
                            timesArcThickness.toPx(),
                            cap = if (moonsetTime != null) StrokeCap.Round else StrokeCap.Butt
                        ),
                        size = 2 * Size(moonTimesRadius.toPx(), moonTimesRadius.toPx()),
                        topLeft = Offset(moonTimesOffset.toPx(), moonTimesOffset.toPx())
                    )
                }
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
                val smallPaint = Paint().apply {
                    color = textColor.toArgb()
                    textSize = smallTextStyle.fontSize.toPx()
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                val largePaint = Paint().apply {
                    color = textColor.toArgb()
                    textSize = largeTextStyle.fontSize.toPx()
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                val midnightPath = Path().apply {
                    arcTo(
                        RectF(
                            0f,
                            outerPadding.toPx() - smallPaint.fontMetrics.descent,
                            size.width,
                            size.height - outerPadding.toPx() + smallPaint.fontMetrics.descent
                        ), -180f, 180f, true
                    )
                }
                canvas.drawTextOnPath("Midnight", midnightPath, 0f, 0f, smallPaint)

                val noonPath = Path().apply {
                    arcTo(
                        RectF(
                            0f,
                            outerPadding.toPx() + smallPaint.fontMetrics.ascent,
                            size.width,
                            size.height - outerPadding.toPx() - smallPaint.fontMetrics.ascent
                        ), 180f, -180f, true
                    )
                }
                canvas.drawTextOnPath("Noon", noonPath, 0f, 0f, smallPaint)

                val currentTimePath = Path().apply {
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
    if (it < Duration.days(0)) it + Duration.days(1) else it
}.toDouble(DurationUnit.DAYS).toFloat() * 360

private fun Instant.toAngle(timeZone: TimeZone): Float {
    val localDateTime = toLocalDateTime(timeZone)
    val localMidnightInstant = localDateTime.run { LocalDateTime(year, month, dayOfMonth, 0, 0) }.toInstant(timeZone)
    return (this - localMidnightInstant).toAngle()
}

@Suppress("unused")
class Previews {
    private val defaultTimeZone = TimeZone.of("America/New_York")

    private val defaultCurrentTime = LocalDateTime(2021, 1, 1, 11, 0).toInstant(defaultTimeZone)

    private val defaultSunriseTime = LocalDateTime(2021, 1, 2, 7, 30).toInstant(defaultTimeZone)
    private val defaultSunsetTime = LocalDateTime(2021, 1, 1, 18, 0).toInstant(defaultTimeZone)

    private val defaultMoonriseTime = LocalDateTime(2021, 1, 2, 9, 0).toInstant(defaultTimeZone)
    private val defaultMoonsetTime = LocalDateTime(2021, 1, 1, 20, 0).toInstant(defaultTimeZone)

    @Preview
    @Composable
    fun Standard_Light() {
        SolunaTheme {
            Surface(Modifier.aspectRatio(1f), color = SolunaTheme.colors.background) {
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

    @Preview(uiMode = UI_MODE_NIGHT_YES)
    @Composable
    fun Standard_Dark() {
        SolunaTheme {
            Surface(Modifier.aspectRatio(1f), color = SolunaTheme.colors.background) {
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

    @Preview(fontScale = 1.75f)
    @Composable
    fun LargeText() {
        SolunaTheme {
            Surface(Modifier.aspectRatio(1f), color = SolunaTheme.colors.background) {
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

    @Preview(heightDp = 320, widthDp = 320)
    @Composable
    fun SmallBox() {
        SolunaTheme {
            Surface(Modifier.aspectRatio(1f), color = SolunaTheme.colors.background) {
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
                    currentTime = defaultCurrentTime.plus(Duration.hours(12)),
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
}
