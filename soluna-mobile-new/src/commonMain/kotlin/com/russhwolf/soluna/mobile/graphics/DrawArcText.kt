package com.russhwolf.soluna.mobile.graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview


enum class ArcTextDirection {
    Up, Down
}

enum class ArcTextBaseline {
    Inside, Center, Outside
}

expect fun DrawScope.drawTextOnPath(
    text: String,
    path: Path,
    style: TextStyle
)

expect fun DrawScope.lineHeightFromMetrics(style: TextStyle): Float

/**
 * Processes RTL information to always return [TextAlign.Left], [TextAlign.Right], or [TextAlign.Center] for use in
 * drawing APIs that need absolute left/right information. Defaults to [TextAlign.Center] when ambiguous.
 */
internal val TextStyle.effectiveTextAlign
    get() = when {
        // TODO there's edge-cases in here that we ignore for now because this is always English in practice
        textAlign == TextAlign.Left
                || (textAlign == TextAlign.Start && textDirection in listOf(
            TextDirection.Ltr,
            TextDirection.ContentOrLtr
        ))
                || (textAlign == TextAlign.End && textDirection in listOf(
            TextDirection.Rtl,
            TextDirection.ContentOrRtl
        ))
            -> TextAlign.Left

        textAlign == TextAlign.Right
                || (textAlign == TextAlign.End && textDirection in listOf(
            TextDirection.Ltr,
            TextDirection.ContentOrLtr
        ))
                || (textAlign == TextAlign.Start && textDirection in listOf(
            TextDirection.Rtl,
            TextDirection.ContentOrRtl
        ))
            -> TextAlign.Right

        else -> TextAlign.Center
    }

fun DrawScope.drawArcText(
    text: String,
    direction: ArcTextDirection,
    baseline: ArcTextBaseline = ArcTextBaseline.Outside,
    rotation: Float = 0f,
    maxAngle: Float = 180f,
    style: TextStyle = TextStyle.Default,
    topLeft: Offset = Offset.Zero,
    size: Size = Size(this.size.width - topLeft.x, this.size.height - topLeft.y)
) {
    val boundsSize = if (size == Size.Unspecified) this.size else size

    val lineHeight = lineHeightFromMetrics(style)
    val offset = when (direction) {
        ArcTextDirection.Up -> when (baseline) {
            ArcTextBaseline.Inside -> 0f
            ArcTextBaseline.Outside -> lineHeight
            ArcTextBaseline.Center -> lineHeight / 2f
        }

        ArcTextDirection.Down -> when (baseline) {
            ArcTextBaseline.Inside -> -lineHeight
            ArcTextBaseline.Outside -> 0f
            ArcTextBaseline.Center -> -lineHeight / 2f
        }
    }
    val bounds = Rect(
        topLeft.x - offset,
        topLeft.y - offset,
        topLeft.x + boundsSize.width + offset,
        topLeft.y + boundsSize.height + offset
    )
    val path = Path().apply {
        when (direction) {
            ArcTextDirection.Up -> addArc(bounds, maxAngle / 2 + 90, -maxAngle)
            ArcTextDirection.Down -> addArc(bounds, -(maxAngle / 2 + 90), maxAngle)
        }
    }
    rotate(rotation) {
        drawTextOnPath(text, path, style)
    }
}

@Preview
@Composable
fun DrawArcText_Preview() {
    Canvas(
        Modifier
            .width(200.dp)
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        drawCircle(color = Color.Gray)
        drawArcText(
            "Down",
            ArcTextDirection.Down,
            ArcTextBaseline.Inside,
            style = TextStyle(color = Color.Red, fontSize = 20.sp)
        )
        drawArcText(
            "Up",
            ArcTextDirection.Up,
            ArcTextBaseline.Inside,
            style = TextStyle(color = Color.Red, fontSize = 20.sp),
        )
    }
}
