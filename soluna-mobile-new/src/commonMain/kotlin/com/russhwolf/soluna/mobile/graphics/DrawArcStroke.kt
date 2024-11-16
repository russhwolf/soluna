package com.russhwolf.soluna.mobile.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun DrawScope.drawArcStroke(
    color: Color,
    startAngle: Float,
    endAngle: Float,
    strokeWidth: Float,
    startCap: StrokeCap = StrokeCap.Round,
    endCap: StrokeCap = StrokeCap.Round,
    topLeft: Offset = Offset.Zero,
    size: Size = Size(this.size.width - topLeft.x, this.size.height - topLeft.y)
) {
    drawArcStroke(
        startColor = color,
        endColor = color,
        startAngle = startAngle,
        endAngle = endAngle,
        strokeWidth = strokeWidth,
        startCap = startCap,
        endCap = endCap,
        topLeft = topLeft,
        size = size
    )
}

fun DrawScope.drawArcStroke(
    startColor: Color,
    endColor: Color,
    startAngle: Float,
    endAngle: Float,
    strokeWidth: Float,
    startCap: StrokeCap = StrokeCap.Round,
    endCap: StrokeCap = StrokeCap.Round,
    topLeft: Offset = Offset.Zero,
    size: Size = Size(this.size.width - topLeft.x, this.size.height - topLeft.y)
) {
    if (endAngle < startAngle) return

    val rotation = (startAngle + endAngle) / 2 - 180
    val halfSweepAngle = (endAngle - startAngle) / 2
    val brush = Brush.rotatedSweepGradient(
        (startAngle - rotation) / 360f to startColor,
        (endAngle - rotation) / 360f to endColor,
        center = Offset(topLeft.x + size.width / 2, topLeft.y + size.height / 2),
        rotation = rotation,
    )

    drawArc(
        brush = brush,
        startAngle = startAngle,
        sweepAngle = halfSweepAngle,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = startCap),
        topLeft = topLeft,
        size = size
    )

    drawArc(
        brush = brush,
        startAngle = startAngle + halfSweepAngle,
        sweepAngle = halfSweepAngle,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = endCap),
        topLeft = topLeft,
        size = size
    )
}
