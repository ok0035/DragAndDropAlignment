package com.zerosword.feature_main.ui

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RightRoundedDrawerBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    onComplete: @Composable () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .shadow(20.dp, shape = RoundedCornerShape(99.dp))
    )

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val radius = height / 2

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(width - radius, 0f)
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        width - 2 * radius,
                        0f,
                        width,
                        height
                    ),
                    startAngleDegrees = -90f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(0f, height)
                close()
            }

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = Color.Gray
                    asFrameworkPaint().apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            setShadowLayer(
                                20f, // shadow radius
                                0f, // dx offset
                                10f, // dy offset
                                0x80000000 // shadow color (50% opaque black)
                            )
                        }
                    }
                }
                canvas.drawPath(path, paint)
            }

            drawPath(
                path = path,
                color = Color.White
            )

            drawPath(
                path = path,
                color = Color(0xffd8d8d8),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        onComplete()
    }
}

@Composable
fun LeftRoundedDrawerBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    onComplete: @Composable () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .shadow(20.dp, shape = RoundedCornerShape(99.dp))
    )

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationZ = 180f }) {
            val width = size.width
            val height = size.height
            val radius = height / 2

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(width - radius, 0f)
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        width - 2 * radius,
                        0f,
                        width,
                        height
                    ),
                    startAngleDegrees = -90f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(0f, height)
                close()
            }

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = Color.Gray
                    asFrameworkPaint().apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            setShadowLayer(
                                20f, // shadow radius
                                0f, // dx offset
                                10f, // dy offset
                                0x80000000 // shadow color (50% opaque black)
                            )
                        }
                    }
                }
                canvas.drawPath(path, paint)
            }

            drawPath(
                path = path,
                color = Color.White
            )

            drawPath(
                path = path,
                color = Color(0xffd8d8d8),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        onComplete()
    }
}

@Composable
@Preview(showBackground = true)
fun RoundedRectanglePreview() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        LeftRoundedDrawerBox(modifier = Modifier
            .width(52.dp)
            .height(52.dp)) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(52.dp)
            )
        }
    }

}