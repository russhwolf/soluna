package com.russhwolf.soluna.mobile.graphics

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

actual fun DrawScope.lineHeightFromMetrics(style: TextStyle): Float {
    val metrics = Paint().apply {
        textSize = style.fontSize.toPx()
        typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    }.fontMetrics

    return style.fontSize.toPx() - metrics.descent
}

actual fun DrawScope.drawTextOnPath(
    text: String,
    path: Path,
    style: TextStyle
) {
    val paint = Paint().apply {
        color = style.color.toArgb()
        textSize = style.fontSize.toPx()
        textAlign = when (style.effectiveTextAlign) {
            TextAlign.Left -> Paint.Align.LEFT
            TextAlign.Right -> Paint.Align.RIGHT
            else -> Paint.Align.CENTER
        }
        isAntiAlias = true
        typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    }

    drawIntoCanvas {
        it.nativeCanvas.drawTextOnPath(text, path.asAndroidPath(), 0f, 0f, paint)
    }
}

@Preview
@Composable
fun DrawArcText_Preview_Android() {
    DrawArcText_Preview()
}
