package com.russhwolf.soluna.mobile.graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun options() {
    Canvas(
        Modifier
            .width(200.dp)
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        drawArcStroke(
            startColor = Color.Green,
            endColor = Color.Blue,
            startAngle = -30f,
            endAngle = 270f,
            strokeWidth = 40f,
            startCap = StrokeCap.Round,
            endCap = StrokeCap.Butt,
            size = Size(this.size.width / 2, this.size.height / 2),
            topLeft = Offset(0f, this.size.height / 4)
        )
    }
}

@Preview
@Composable
fun defaults() {
    Canvas(
        Modifier
            .width(200.dp)
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        drawArcStroke(
            color = Color.Red,
            startAngle = 90f,
            endAngle = 300f,
            strokeWidth = 40f,
        )
    }
}

