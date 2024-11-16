package com.russhwolf.soluna.mobile.graphics

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush

// See https://issuetracker.google.com/issues/303944825 for possible future first-party implementation
@Stable
fun Brush.Companion.rotatedSweepGradient(
    vararg colorStops: Pair<Float, Color>,
    center: Offset = Offset.Unspecified,
    rotation: Float = 0f
): Brush = RotatedSweepGradient(
    colors = List<Color>(colorStops.size) { i -> colorStops[i].second },
    stops = List<Float>(colorStops.size) { i -> colorStops[i].first },
    center = center,
    rotation = rotation
)

internal expect fun createRotatedSweepGradientShader(
    size: Size,
    center: Offset,
    colors: List<Color>,
    stops: List<Float>? = null,
    rotation: Float = 0f
): Shader

@Immutable
internal class RotatedSweepGradient internal constructor(
    private val center: Offset,
    private val colors: List<Color>,
    private val stops: List<Float>? = null,
    private val rotation: Float = 0f,
) : ShaderBrush() {

    override fun createShader(size: Size): Shader =
        createRotatedSweepGradientShader(size, center, colors, stops, rotation)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RotatedSweepGradient) return false

        if (center != other.center) return false
        if (colors != other.colors) return false
        if (stops != other.stops) return false
        if (rotation != other.rotation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = center.hashCode()
        result = 31 * result + colors.hashCode()
        result = 31 * result + (stops?.hashCode() ?: 0)
        result = 31 * result + rotation.hashCode()
        return result
    }

    override fun toString(): String {
        val centerValue = if (center.isSpecified) "center=$center, " else ""
        return "RotatedSweepGradient(" +
                centerValue +
                "colors=$colors, stops=$stops, rotation=$rotation)"
    }
}
