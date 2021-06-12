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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import com.russhwolf.soluna.android.extensions.toDisplayTime
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.days
import kotlin.time.hours

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
    val effectiveSunsetTime = sunsetTime ?: currentTime.plus(1.days)
    val effectiveMoonriseTime = moonriseTime ?: currentTime
    val effectiveMoonsetTime = moonsetTime ?: currentTime.plus(1.days)

    val backgroundColor = Color(0x80808080)
    val dayColor = SolunaTheme.colors.primary
    val moonColor = SolunaTheme.colors.secondary
    val currentTimeColor = SolunaTheme.colors.onBackground
    val midnightColor = Color(0x80808080)
    val textColor = SolunaTheme.colors.onBackground

    BoxWithConstraints(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp)
                .aspectRatio(1f)
        ) {

            val timesArcThickness = 24.dp.toPx()
            val timesArcMargin = 8.dp.toPx()
            val moonTimesOffset = 1 * timesArcThickness + 2 * timesArcMargin
            val moonTimesRadius = size.minDimension - moonTimesOffset
            val sunTimesOffset = 3 * timesArcThickness + 4 * timesArcMargin
            val sunTimesRadius = size.minDimension - sunTimesOffset
            val currentTimeWidth = 4.dp.toPx()
            val midnightWidth = 4.dp.toPx()

            drawCircle(
                color = backgroundColor
            )
            drawLine(
                color = midnightColor,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height / 2),
                strokeWidth = midnightWidth,
                cap = StrokeCap.Round
            )

            rotate(-90f) {
                if (sunriseTime != null || sunsetTime != null) {
                    drawArc(
                        color = dayColor,
                        startAngle = effectiveSunriseTime.toAngle(timeZone),
                        sweepAngle = (effectiveSunsetTime - effectiveSunriseTime).toAngle(),
                        useCenter = false,
                        style = Stroke(timesArcThickness, cap = StrokeCap.Round),
                        size = Size(sunTimesRadius, sunTimesRadius),
                        topLeft = Offset(sunTimesOffset, sunTimesOffset) / 2f
                    )

                }
                if (moonriseTime != null || moonsetTime != null) {
                    drawArc(
                        color = moonColor,
                        startAngle = effectiveMoonriseTime.toAngle(timeZone),
                        sweepAngle = (effectiveMoonsetTime - effectiveMoonriseTime).toAngle(),
                        useCenter = false,
                        style = Stroke(timesArcThickness, cap = StrokeCap.Round),
                        size = Size(moonTimesRadius, moonTimesRadius),
                        topLeft = Offset(moonTimesOffset, moonTimesOffset) / 2f
                    )
                }
            }
            rotate(currentTime.toAngle(timeZone)) {
                drawLine(
                    color = currentTimeColor,
                    start = Offset(size.width / 2, size.height / 2),
                    end = Offset(size.width / 2, 0f),
                    strokeWidth = currentTimeWidth,
                    cap = StrokeCap.Round,
                )
            }

            drawIntoCanvas {
                val canvas = it.nativeCanvas
                val smallPaint = Paint().apply {
                    color = textColor.toArgb()
                    textSize = 14.sp.toPx()
                    textAlign = Paint.Align.CENTER
                }
                val largePaint = Paint().apply {
                    color = textColor.toArgb()
                    textSize = 32.sp.toPx()
                    textAlign = Paint.Align.CENTER
                }
                val midnightPath = Path().apply {
                    arcTo(
                        RectF(
                            -smallPaint.fontMetrics.bottom,
                            -smallPaint.fontMetrics.bottom,
                            size.width + smallPaint.fontMetrics.bottom,
                            size.height + smallPaint.fontMetrics.bottom
                        ), -180f, 180f, true
                    )
                }
                canvas.drawTextOnPath("Midnight", midnightPath, 0f, 0f, smallPaint)

                val noonPath = Path().apply {
                    arcTo(
                        RectF(
                            smallPaint.fontMetrics.top,
                            smallPaint.fontMetrics.top,
                            size.width - smallPaint.fontMetrics.top,
                            size.height - smallPaint.fontMetrics.top
                        ), 180f, -180f, true
                    )
                }
                canvas.drawTextOnPath("Noon", noonPath, 0f, 0f, smallPaint)

                val currentTimePath = Path().apply {
                    val angle = currentTime.toAngle(timeZone)
                    if (currentTime.toLocalDateTime(timeZone).hour in 6 until 18) {
                        val offset = -largePaint.fontMetrics.top + smallPaint.fontMetrics.bottom + 8.dp.toPx()
                        arcTo(RectF(-offset, -offset, size.width + offset, size.height + offset), angle, -180f, true)
                    } else {
                        val offset = largePaint.fontMetrics.bottom + smallPaint.fontMetrics.bottom + 8.dp.toPx()
                        arcTo(
                            RectF(-offset, -offset, size.width + offset, size.height + offset),
                            angle - 180f,
                            180f,
                            true
                        )
                    }
                }
                canvas.drawTextOnPath(currentTime.toDisplayTime(timeZone), currentTimePath, 0f, 0f, largePaint)

//                val radius = size.minDimension/2 + smallPaint.fontMetrics.run { (bottom - top) / 2f }
//                val offset = size.minDimension/2
//                for (i in (1..11) + (13..23)) {
//                    val tau = 2 * PI
//                    val angle = (i/24f * tau).toFloat()
//                    canvas.drawText(
//                        i.toString(),
//                        offset + radius * sin(angle),
//                        offset + smallPaint.fontMetrics.bottom - radius * cos(angle),
//                        smallPaint
//                    )
//                }
            }
        }

        val size = minOf(maxHeight, maxWidth)

        TimeIcon(
            angle = sunriseTime?.toAngle(timeZone),
            radius = size / 2 - 100.dp,
            tint = textColor,
            icon = Icons.Filled.LightMode
        )
        TimeIcon(
            angle = sunsetTime?.toAngle(timeZone),
            radius = size / 2 - 100.dp,
            tint = textColor,
            icon = Icons.Outlined.LightMode
        )
        TimeIcon(
            angle = moonriseTime?.toAngle(timeZone),
            radius = size / 2 - 68.dp,
            tint = textColor,
            icon = Icons.Filled.DarkMode
        )
        TimeIcon(
            angle = moonsetTime?.toAngle(timeZone),
            radius = size / 2 - 68.dp,
            tint = textColor,
            icon = Icons.Outlined.DarkMode
        )
    }
}

@Composable
private fun TimeIcon(angle: Float?, radius: Dp, tint: Color, icon: ImageVector) {
    if (angle != null) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .rotate(180 + angle)
                .offset(0.dp, radius)
                .rotate(-(180 + angle)),
            tint = tint
        )
    }
}

private fun Duration.toAngle(): Float = let { if (it < 0.days) it + 1.days else it }.inDays.toFloat() * 360
private fun Instant.toAngle(timeZone: TimeZone): Float {
    val localDateTime = toLocalDateTime(timeZone)
    val localMidnightInstant = localDateTime.run { LocalDateTime(year, month, dayOfMonth, 0, 0) }.toInstant(timeZone)
    return (this - localMidnightInstant).toAngle()
}

class Previews {
    private val defaultTimeZone = TimeZone.UTC

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
                    currentTime = defaultCurrentTime.plus(11.hours),
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
