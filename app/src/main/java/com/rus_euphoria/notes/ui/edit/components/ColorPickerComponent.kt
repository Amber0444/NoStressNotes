package com.rus_euphoria.notes.ui.edit.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ColorPickerDialog(
    initialColor: Int,
    onColorConfirmed: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val hsv = remember {
        FloatArray(3).also { AndroidColor.colorToHSV(initialColor, it) }
    }

    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var saturation by remember { mutableFloatStateOf(hsv[1]) }
    var brightness by remember { mutableFloatStateOf(hsv[2]) }

    val currentColor = Color.hsv(hue, saturation, brightness)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentColor)
                )
                Text("Pick a color", style = MaterialTheme.typography.headlineMedium)
            }

            ColorWheel(
                hue = hue,
                saturation = saturation,
                brightness = brightness,
                currentColor = currentColor,
                onHueAndSaturationChanged = { h, s ->
                    hue = h
                    saturation = s
                }
            )

            Text("Brightness", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = brightness,
                onValueChange = { brightness = it },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = currentColor,
                    activeTrackColor = currentColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                TextButton(onClick = { onColorConfirmed(currentColor.toArgb()) }) {
                    Text("OK")
                }
            }
        }
    }
}

@Composable
private fun ColorWheel(
    hue: Float,
    saturation: Float,
    brightness: Float,
    currentColor: Color,
    onHueAndSaturationChanged: (Float, Float) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val r = min(cx, cy)
                        offsetToHS(offset, cx, cy, r)?.let { (h, s) ->
                            onHueAndSaturationChanged(h, s)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val r = min(cx, cy)
                        offsetToHS(change.position, cx, cy, r)?.let { (h, s) ->
                            onHueAndSaturationChanged(h, s)
                        }
                    }
                }
        ) {
            drawColorWheel(brightness)
            drawSelector(hue, saturation, brightness)
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .drawBehind {
                    drawCircle(currentColor)
                    drawCircle(Color.White, style = Stroke(width = 3.dp.toPx()))
                }
        )
    }
}

private fun offsetToHS(offset: Offset, cx: Float, cy: Float, radius: Float): Pair<Float, Float>? {
    val x = offset.x - cx
    val y = offset.y - cy
    if (sqrt(x * x + y * y) > radius) return null
    val angle = (Math.toDegrees(atan2(y.toDouble(), x.toDouble())) + 360) % 360
    val sat = (sqrt(x * x + y * y) / radius).coerceIn(0f, 1f)
    return angle.toFloat() to sat
}

private fun DrawScope.drawColorWheel(brightness: Float) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val radius = min(cx, cy)
    for (angle in 0 until 360 step 2) {
        for (r in 0..radius.toInt() step 3) {
            val sat = r / radius
            val rad = Math.toRadians(angle.toDouble())
            drawCircle(
                color = Color.hsv(angle.toFloat(), sat, brightness),
                radius = 4f,
                center = Offset(cx + r * cos(rad).toFloat(), cy + r * sin(rad).toFloat())
            )
        }
    }
}

private fun DrawScope.drawSelector(hue: Float, saturation: Float, brightness: Float) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val radius = min(cx, cy)
    val rad = Math.toRadians(hue.toDouble())
    val dist = saturation * radius
    val x = cx + dist * cos(rad).toFloat()
    val y = cy + dist * sin(rad).toFloat()
    val cross = 12.dp.toPx()
    val sw = 2.dp.toPx()
    val lc = if (brightness > 0.5f) Color.Black else Color.White
    drawLine(lc, Offset(x - cross, y), Offset(x + cross, y), sw, cap = StrokeCap.Round)
    drawLine(lc, Offset(x, y - cross), Offset(x, y + cross), sw, cap = StrokeCap.Round)
    drawCircle(lc, radius = 8.dp.toPx(), center = Offset(x, y), style = Stroke(sw))
}
