package com.russhwolf.soluna.mobile.graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.mobile.theme.SolunaTheme
import com.russhwolf.soluna.mobile.util.formatTime
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
import org.jetbrains.compose.resources.stringResource
import soluna.soluna_mobile_new.generated.resources.Res
import soluna.soluna_mobile_new.generated.resources.midnight
import soluna.soluna_mobile_new.generated.resources.noon

@Composable
fun SunMoonTimesGraphic(
    state: SunMoonTimesGraphicState,
    modifier: Modifier = Modifier
) {
    val colors = SolunaTheme.colorScheme
    val typography = SolunaTheme.typography

    val midnight = when (state) {
        is SunMoonTimesGraphicState.Daily -> LocalDateTime(state.date, LocalTime(0, 0)).toInstant(state.timeZone)
        is SunMoonTimesGraphicState.Next -> state.currentTime.midnightBefore(state.timeZone)
    }
    val startTime = when (state) {
        is SunMoonTimesGraphicState.Daily -> midnight
        is SunMoonTimesGraphicState.Next -> state.currentTime
    }
    val currentLocalTime = when (state) {
        is SunMoonTimesGraphicState.Daily -> LocalTime(0, 0) // Will not be used
        is SunMoonTimesGraphicState.Next -> state.currentTime.toLocalDateTime(state.timeZone).time
    }
    val currentTimeString = currentLocalTime.formatTime()

    val timesArcThickness = 32.dp
    val innerPadding = 8.dp
    val labelPadding = 4.dp
    val handWidth = 4.dp
    val minSize = (2 * timesArcThickness + 3 * innerPadding) * 2

    val labelStyle = typography.titleSmall.copy(color = colors.onSurface)
    val timeStyle = typography.titleMedium.copy(color = colors.onSurface)

    val sunrisePainter = rememberVectorPainter(Icons.Filled.LightMode)
    val sunsetPainter = rememberVectorPainter(Icons.Outlined.LightMode)
    val moonrisePainter = rememberVectorPainter(Icons.Filled.DarkMode)
    val moonsetPainter = rememberVectorPainter(Icons.Outlined.DarkMode)

    val midnightString = stringResource(Res.string.midnight)
    val noonString = stringResource(Res.string.noon)

    BoxWithConstraints(
        Modifier
            .aspectRatio(1f)
            .defaultMinSize(minSize, minSize)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {

        Canvas(Modifier.fillMaxSize()) {

            val backgroundRadius = minOf(maxWidth, maxHeight).toPx() / 2 -
                    2 * labelPadding.toPx() - labelStyle.fontSize.toPx() - timeStyle.fontSize.toPx()
            val sunArcRadius = backgroundRadius - timesArcThickness.toPx() / 2 - innerPadding.toPx()
            val moonArcRadius = backgroundRadius - 3 * timesArcThickness.toPx() / 2 - innerPadding.toPx() * 2
            val labelRadius = backgroundRadius + labelPadding.toPx()
            val timeRadius = labelRadius + labelPadding.toPx() + labelStyle.fontSize.toPx()

            drawCircle(colors.surfaceContainer, radius = backgroundRadius)

            drawCircle(
                colors.surfaceContainerHighest,
                radius = sunArcRadius,
                style = Stroke(width = timesArcThickness.toPx())
            )
            drawCircle(
                colors.surfaceContainerHighest,
                radius = moonArcRadius,
                style = Stroke(width = timesArcThickness.toPx())
            )

            drawArcText(
                text = midnightString,
                direction = ArcTextDirection.Down,
                baseline = ArcTextBaseline.Outside,
                style = labelStyle,
                radius = labelRadius
            )
            drawArcText(
                text = noonString,
                direction = ArcTextDirection.Up,
                baseline = ArcTextBaseline.Outside,
                style = labelStyle,
                radius = labelRadius
            )

            if (state is SunMoonTimesGraphicState.Next) {
                val isCurrentTimeOnBottom = currentLocalTime in LocalTime(6, 0)..LocalTime(18, 0)
                drawArcText(
                    text = currentTimeString,
                    direction = if (isCurrentTimeOnBottom) ArcTextDirection.Up else ArcTextDirection.Down,
                    baseline = ArcTextBaseline.Outside,
                    style = timeStyle,
                    radius = timeRadius,
                    rotation = state.currentTime.angleFrom(
                        midnight,
                        state.timeZone
                    ) + if (isCurrentTimeOnBottom) 180 else 0
                )
            }

            rotate(-90f) {
                drawTimesArc(
                    times = state.sunTimes,
                    timeZone = state.timeZone,
                    midnight = midnight,
                    startTime = startTime,
                    startColor = colors.sunriseContainer,
                    endColor = colors.sunsetContainer,
                    strokeWidth = timesArcThickness.toPx(),
                    radius = sunArcRadius
                )

                drawTimesArc(
                    times = state.moonTimes,
                    timeZone = state.timeZone,
                    midnight = midnight,
                    startTime = startTime,
                    startColor = colors.moonriseContainer,
                    endColor = colors.moonsetContainer,
                    strokeWidth = timesArcThickness.toPx(),
                    radius = moonArcRadius
                )
            }

            fun Instant.toTimeIconAngle() = takeIf { it in startTime..startTime.plusOneDay(state.timeZone) }
                ?.angleFrom(midnight, state.timeZone)

            drawIcon(
                painter = sunrisePainter,
                angle = state.sunTimes.riseOrNull?.toTimeIconAngle(),
                radius = sunArcRadius,
                size = timesArcThickness.toPx(),
                color = colors.onSunriseContainer
            )
            drawIcon(
                painter = sunsetPainter,
                angle = state.sunTimes.setOrNull?.toTimeIconAngle(),
                radius = sunArcRadius,
                size = timesArcThickness.toPx(),
                color = colors.onSunsetContainer
            )
            drawIcon(
                painter = moonrisePainter,
                angle = state.moonTimes.riseOrNull?.toTimeIconAngle(),
                radius = moonArcRadius,
                size = timesArcThickness.toPx(),
                color = colors.onMoonriseContainer
            )
            drawIcon(
                painter = moonsetPainter,
                angle = state.moonTimes.setOrNull?.toTimeIconAngle(),
                radius = moonArcRadius,
                size = timesArcThickness.toPx(),
                color = colors.onMoonsetContainer
            )

            drawLine(
                color = colors.outline,
                start = center,
                end = Offset(center.x, center.y - backgroundRadius),
                strokeWidth = handWidth.toPx(),
                cap = StrokeCap.Round
            )

            if (state is SunMoonTimesGraphicState.Next) {
                rotate(state.currentTime.angleFrom(midnight, state.timeZone)) {
                    drawLine(
                        color = colors.onSurface,
                        start = center,
                        end = Offset(center.x, center.y - backgroundRadius),
                        strokeWidth = handWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawIcon(
    painter: VectorPainter,
    angle: Float?,
    radius: Float,
    size: Float,
    color: Color,
    center: Offset = this.center
) {
    angle ?: return

    translate(center.x - size / 2, center.y - size / 2) {
        rotate(180f + angle) {
            translate(0f, radius) {
                rotate(-(180f + angle)) {
                    with(painter) {
                        draw(Size(size, size), colorFilter = ColorFilter.tint(color))
                    }
                }
            }
        }
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
    radius: Float,
    center: Offset = this.center
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
