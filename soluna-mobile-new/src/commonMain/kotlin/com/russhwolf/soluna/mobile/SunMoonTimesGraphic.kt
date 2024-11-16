package com.russhwolf.soluna.mobile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.mobile.graphics.drawArcStroke
import com.russhwolf.soluna.riseOrNull
import com.russhwolf.soluna.setOrNull
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

enum class SunMoonTimesGraphicMode {
    Daily, Next
}

@Composable
fun SunMoonTimesGraphic(
    currentTime: Instant,
    sunTimes: RiseSetResult<Instant>,
    moonTimes: RiseSetResult<Instant>,
    timeZone: TimeZone,
    mode: SunMoonTimesGraphicMode = SunMoonTimesGraphicMode.Daily,
    modifier: Modifier = Modifier
) {
    val midnight = currentTime.midnightBefore(timeZone)
    val startTime = when (mode) {
        SunMoonTimesGraphicMode.Daily -> midnight
        SunMoonTimesGraphicMode.Next -> currentTime
    }

    val timesArcThickness = 32.dp
    val innerMargin = 8.dp
    val minSize = (2 * timesArcThickness + 3 * innerMargin) * 2

    BoxWithConstraints(
        Modifier
            .aspectRatio(1f)
            .defaultMinSize(minSize, minSize)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        val size = minOf(maxWidth, maxHeight)
        val sunArcRadius = size / 2 - timesArcThickness / 2 - innerMargin
        val moonArcRadius = size / 2 - 3 * timesArcThickness / 2 - innerMargin * 2
        val center = DpOffset(maxWidth / 2, maxHeight / 2)

        Canvas(Modifier.fillMaxSize()) {
            val centerPx = Offset(center.x.toPx(), center.y.toPx())

            drawCircle(Color.Gray)

            rotate(-90f) {
                drawTimesArc(
                    times = sunTimes,
                    timeZone = timeZone,
                    midnight = midnight,
                    startTime = startTime,
                    startColor = Color.Yellow,
                    endColor = Color.Red,
                    strokeWidth = timesArcThickness.toPx(),
                    center = centerPx,
                    radius = sunArcRadius.toPx()
                )

                drawTimesArc(
                    times = moonTimes,
                    timeZone = timeZone,
                    midnight = midnight,
                    startTime = startTime,
                    startColor = Color.Blue,
                    endColor = Color.Magenta,
                    strokeWidth = timesArcThickness.toPx(),
                    center = centerPx,
                    radius = moonArcRadius.toPx()
                )
            }

            drawLine(
                color = Color.White,
                start = centerPx,
                end = Offset(centerPx.x, 0f),
                strokeWidth = 8.dp.toPx(),
                cap = StrokeCap.Round
            )

            rotate(currentTime.angleFrom(midnight, timeZone)) {
                drawLine(
                    color = Color.Black,
                    start = centerPx,
                    end = Offset(centerPx.x, 0f),
                    strokeWidth = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        TimeIcon(
            angle = sunTimes.riseOrNull?.takeIf { it > startTime }?.angleFrom(midnight, timeZone),
            radius = sunArcRadius,
            size = timesArcThickness,
            iconColor = Color.White,
            icon = Icons.Filled.LightMode
        )
        TimeIcon(
            angle = sunTimes.setOrNull?.takeIf { it > startTime }?.angleFrom(midnight, timeZone),
            radius = sunArcRadius,
            size = timesArcThickness,
            iconColor = Color.White,
            icon = Icons.Outlined.LightMode
        )
        TimeIcon(
            angle = moonTimes.riseOrNull?.takeIf { it > startTime }?.angleFrom(midnight, timeZone),
            radius = moonArcRadius,
            size = timesArcThickness,
            iconColor = Color.White,
            icon = Icons.Filled.DarkMode
        )
        TimeIcon(
            angle = moonTimes.setOrNull?.takeIf { it > startTime }?.angleFrom(midnight, timeZone),
            radius = moonArcRadius,
            size = timesArcThickness,
            iconColor = Color.White,
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

private fun Instant.angleFrom(other: Instant, timeZone: TimeZone): Float {
    val thisDateTime = toLocalDateTime(timeZone)
    val otherDateTime = other.toLocalDateTime(timeZone)

    val days = otherDateTime.date.daysUntil(thisDateTime.date)
    val seconds = thisDateTime.time.toSecondOfDay() - otherDateTime.time.toSecondOfDay()
    return (days + seconds / 86400f) * 360f
}

private fun Instant.midnightBefore(timeZone: TimeZone): Instant =
    LocalDateTime(this.toLocalDateTime(timeZone).date, LocalTime(0, 0)).toInstant(timeZone)

private fun Instant.plusOneDay(timeZone: TimeZone): Instant =
    this.plus(1, DateTimeUnit.DAY, timeZone)


private fun DrawScope.drawTimesArc(
    times: RiseSetResult<Instant>,
    timeZone: TimeZone,
    midnight: Instant,
    startTime: Instant,
    startColor: Color,
    endColor: Color,
    strokeWidth: Float,
    center: Offset,
    radius: Float
) {
    val endTime = startTime.plusOneDay(timeZone)

    val size = Size(2 * radius, 2 * radius)
    val topLeft = center - Offset(radius, radius)

    when (times) {
        is RiseSetResult.RiseThenSet -> {
            val (riseTime, setTime) = times

            val arcStart = maxOf(startTime, riseTime)
            val arcEnd = minOf(setTime, endTime)

            drawArcStroke(
                startColor = startColor,
                endColor = endColor,
                startAngle = arcStart.angleFrom(midnight, timeZone),
                endAngle = arcEnd.angleFrom(midnight, timeZone),
                strokeWidth = strokeWidth,
                startCap = if (arcStart == startTime) StrokeCap.Butt else StrokeCap.Round,
                endCap = if (arcEnd == endTime) StrokeCap.Butt else StrokeCap.Round,
                topLeft = topLeft,
                size = size
            )
        }

        is RiseSetResult.SetThenRise -> {
            val (setTime, riseTime) = times

            val arc1End = maxOf(startTime, setTime)
            val arc2Start = minOf(riseTime, endTime)

            drawArcStroke(
                startColor = startColor,
                endColor = endColor,
                startAngle = startTime.angleFrom(midnight, timeZone),
                endAngle = arc1End.angleFrom(midnight, timeZone),
                strokeWidth = strokeWidth,
                startCap = StrokeCap.Butt,
                topLeft = topLeft,
                size = size
            )
            drawArcStroke(
                startColor = startColor,
                endColor = endColor,
                startAngle = arc2Start.angleFrom(midnight, timeZone),
                endAngle = endTime.angleFrom(midnight, timeZone),
                strokeWidth = strokeWidth,
                endCap = StrokeCap.Butt,
                topLeft = topLeft,
                size = size
            )
        }

        is RiseSetResult.RiseOnly -> {
            drawArcStroke(
                startColor = startColor,
                endColor = endColor,
                startAngle = times.riseTime.angleFrom(midnight, timeZone),
                endAngle = endTime.angleFrom(midnight, timeZone),
                strokeWidth = strokeWidth,
                startCap = StrokeCap.Round,
                endCap = StrokeCap.Butt,
                topLeft = topLeft,
                size = size
            )
        }

        is RiseSetResult.SetOnly -> {
            drawArcStroke(
                startColor = startColor,
                endColor = endColor,
                startAngle = startTime.angleFrom(midnight, timeZone),
                endAngle = times.setTime.angleFrom(midnight, timeZone),
                strokeWidth = strokeWidth,
                startCap = StrokeCap.Butt,
                endCap = StrokeCap.Round,
                topLeft = topLeft,
                size = size
            )
        }

        is RiseSetResult.UpAllDay -> {
            drawArcStroke(
                startColor = startColor,
                endColor = endColor,
                startAngle = startTime.angleFrom(midnight, timeZone),
                endAngle = endTime.angleFrom(midnight, timeZone),
                strokeWidth = strokeWidth,
                startCap = StrokeCap.Butt,
                endCap = StrokeCap.Round,
                topLeft = topLeft,
                size = size
            )
        }

        is RiseSetResult.DownAllDay,
        is RiseSetResult.Unknown -> {
            // Draw nothing
        }
    }
}
