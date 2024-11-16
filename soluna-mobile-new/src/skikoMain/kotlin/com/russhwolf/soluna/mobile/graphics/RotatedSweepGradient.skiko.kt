package com.russhwolf.soluna.mobile.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.toArgb
import org.jetbrains.skia.FilterTileMode
import org.jetbrains.skia.GradientStyle

internal actual fun createRotatedSweepGradientShader(
    size: Size,
    center: Offset,
    colors: List<Color>,
    stops: List<Float>?,
    rotation: Float,
): Shader {
    val shaderCenter = if (center.isUnspecified) {
        size.center
    } else {
        Offset(
            if (center.x == Float.POSITIVE_INFINITY) size.width else center.x,
            if (center.y == Float.POSITIVE_INFINITY) size.height else center.y
        )
    }
    val sweepGradientShader = AngledSweepGradientShader(
        shaderCenter,
        colors,
        stops,
        rotation
    )
    return sweepGradientShader
}

private fun AngledSweepGradientShader(
    center: Offset,
    colors: List<Color>,
    colorStops: List<Float>?,
    startAngle: Float,
): Shader {
    validateColorStops(colors, colorStops)
    return Shader.makeSweepGradient(
        x = center.x,
        y = center.y,
        startAngle = 0f + startAngle,
        endAngle = 360f + startAngle,
        colors = colors.toIntArray(),
        positions = colorStops?.toFloatArray(),
        style = GradientStyle.DEFAULT.withTileMode(FilterTileMode.REPEAT)
    )
}

private fun List<Color>.toIntArray(): IntArray =
    IntArray(size) { i -> this[i].toArgb() }

private fun validateColorStops(colors: List<Color>, colorStops: List<Float>?) {
    if (colorStops == null) {
        if (colors.size < 2) {
            throw IllegalArgumentException(
                "colors must have length of at least 2 if colorStops " +
                        "is omitted."
            )
        }
    } else if (colors.size != colorStops.size) {
        throw IllegalArgumentException(
            "colors and colorStops arguments must have" +
                    " equal length."
        )
    }
}
