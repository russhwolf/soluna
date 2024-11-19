package com.russhwolf.soluna.mobile.graphics

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asSkiaPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PathMeasure
import org.jetbrains.skia.Point
import org.jetbrains.skia.RSXform
import org.jetbrains.skia.TextBlobBuilder

private fun DrawScope.getFontFromStyle(style: TextStyle): Font {
    // TODO using default/empty typeface returns zero for metrics and glyph measurements. Need better fallback.
//    val typeface = Typeface.makeEmpty()
//    val typeface = FontCollection().setEnableFallback(true).defaultFallback()
    val typeface = FontMgr.default.matchFamilyStyle("Helvetica Neue", FontStyle.NORMAL)!!
    val font = Font(typeface, style.fontSize.toPx())
    return font
}

actual fun DrawScope.lineHeightFromMetrics(style: TextStyle): Float {
    val metrics = getFontFromStyle(style).metrics
    return style.fontSize.toPx() - metrics.descent
}

// Adapted from https://github.com/kirill-grouchnikov/artemis/blob/9cd09643a55da0475b88b545caf056395f9ab42e/src/main/kotlin/org/pushingpixels/artemis/DrawTextOnPath.kt
/*
 * Copyright (c) 2021-24 Artemis, Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of the copyright holder nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
actual fun DrawScope.drawTextOnPath(
    text: String,
    path: Path,
    style: TextStyle
) {
    val pathMeasure = PathMeasure(path.asSkiaPath())
    val pathPixelLength = pathMeasure.length
    if (pathPixelLength <= 0f) return

    val font = getFontFromStyle(style)

    val glyphs = font.getStringGlyphs(text)
    val glyphWidths = font.getWidths(glyphs)
    val glyphPositions = font.getPositions(glyphs, Point.ZERO)

    val textPixelLength = glyphPositions[glyphs.size - 1].x + glyphWidths[glyphs.size - 1]
    // Where do we start to draw the first glyph along the path based on the requested
    // text alignment
    val textStartOffset = when (style.effectiveTextAlign) {
        TextAlign.Left -> glyphPositions[0].x
        TextAlign.Right -> pathPixelLength - textPixelLength + glyphPositions[0].x
        else -> (pathPixelLength - textPixelLength) / 2.0f + glyphPositions[0].x
    }

    val visibleGlyphs = arrayListOf<Short>()
    val visibleGlyphTransforms = arrayListOf<RSXform>()

    for (index in glyphs.indices) {
        val glyphStartOffset = glyphPositions[index]
        val glyphWidth = glyphWidths[index]
        // We're going to be rotating each glyph around its mid-horizontal point
        val glyphMidPointOffset = textStartOffset + glyphStartOffset.x + glyphWidth / 2.0f

        // There's no good solution for drawing glyphs that overflow at one of the ends of
        // the path (if the path is not long enough to position all the glyphs). Here we drop
        // (clip) the leading and the trailing glyphs
        if (!(glyphMidPointOffset >= 0.0f) || !(glyphMidPointOffset < pathPixelLength)) continue

        val glyphMidPointOnPath = pathMeasure.getPosition(glyphMidPointOffset)!!
        val glyphMidPointTangent = pathMeasure.getTangent(glyphMidPointOffset)!!

        var translationX = glyphMidPointOnPath.x
        var translationY = glyphMidPointOnPath.y

        // Horizontal offset based on the tangent
        translationX -= glyphMidPointTangent.x * glyphWidth / 2.0f
        translationY -= glyphMidPointTangent.y * glyphWidth / 2.0f

        // Vertically offset based on the normal vector
        // [-glyphMidPointTangent.y, glyphMidPointTangent.x]
        val glyphY = glyphPositions[index].y
        translationX -= glyphY * glyphMidPointTangent.y
        translationY += glyphY * glyphMidPointTangent.x

        // Compute the combined rotation-scale transformation matrix to be applied on
        // the current glyph
        visibleGlyphTransforms.add(
            RSXform(
                scos = glyphMidPointTangent.x,
                ssin = glyphMidPointTangent.y,
                tx = translationX,
                ty = translationY
            )
        )
        visibleGlyphs.add(glyphs[index])
    }

    val textBlob = TextBlobBuilder().apply {
        appendRunRSXform(
            font = font,
            glyphs = visibleGlyphs.toShortArray(),
            xform = visibleGlyphTransforms.toTypedArray()
        )
    }.build()!!

    val paint = Paint()
    paint.color = style.color.toArgb()

    drawIntoCanvas {
        it.nativeCanvas.drawTextBlob(
            blob = textBlob,
            x = 0f,
            y = 0f,
            paint = paint
        )
    }
}
