package com.russhwolf.soluna.mobile.graphics

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp


@Preview
@Composable
private fun Previews() {
    Column {
        Canvas(
            Modifier
                .background(Color.Gray)
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

        Spacer(Modifier.height(16.dp))

        Canvas(
            Modifier
                .background(Color.Gray)
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
}
