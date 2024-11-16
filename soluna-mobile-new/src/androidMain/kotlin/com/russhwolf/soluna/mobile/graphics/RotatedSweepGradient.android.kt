package com.russhwolf.soluna.mobile.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.SweepGradientShader
import androidx.core.graphics.transform

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
    val sweepGradientShader = SweepGradientShader(
        shaderCenter,
        colors,
        stops
    )
    sweepGradientShader.transform { setRotate(rotation, shaderCenter.x, shaderCenter.y) }
    return sweepGradientShader
}
